import { Component, ElementRef, OnInit, ViewChild ,inject} from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { InputTextModule } from 'primeng/inputtext';
import { MultiSelectModule } from 'primeng/multiselect';
import { SelectModule } from 'primeng/select';
import { SliderModule } from 'primeng/slider';
import { Table, TableModule } from 'primeng/table';
import { ProgressBarModule } from 'primeng/progressbar';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { ToastModule } from 'primeng/toast';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { RatingModule } from 'primeng/rating';
import { RippleModule } from 'primeng/ripple';
import { InputIconModule } from 'primeng/inputicon';
import { IconFieldModule } from 'primeng/iconfield';
import { TagModule } from 'primeng/tag';
import { Customer, CustomerService, Representative } from '../../service/customer.service';
import { Product, ProductService } from '../../service/product.service';
import {ObjectUtils} from "primeng/utils";
import { MenubarModule } from 'primeng/menubar';
import { StepperModule } from 'primeng/stepper';
import { TabsModule } from 'primeng/tabs';
import { ButtonGroupModule } from 'primeng/buttongroup';
import { ToolbarModule } from 'primeng/toolbar';
import { DatePickerModule } from 'primeng/datepicker';
import { InventoryMovementDto, InventoryService } from '@/pages/service/Inventory.service';
import { OrderDto, OrderItemsDto, OrderService } from '@/pages/service/order.service';
interface expandedRows {
    [key: string]: boolean;
}

@Component({
    selector: 'app-order-demo',
    standalone: true,
    imports: [
        TableModule,
        MultiSelectModule,
        SelectModule,
        InputIconModule,
        TagModule,
        InputTextModule,
        SliderModule,
        ProgressBarModule,
        ToolbarModule,
        ToggleButtonModule,
        ToastModule,
        CommonModule,
        FormsModule,
        ButtonModule,
        RatingModule,
        TabsModule,
        MenubarModule,
        StepperModule,
        RippleModule,
        IconFieldModule,
        ButtonGroupModule,
        DatePickerModule
    ],
    templateUrl: `./orderLayout.html`,
    styles: `
        .p-datatable-frozen-tbody {
            font-weight: bold;
        }

        .p-datatable-scrollable .p-frozen-column {
            font-weight: bold;
        }
    `,
    providers: [ConfirmationService, MessageService, CustomerService, ProductService]
})
export class OrderLayout implements OnInit {
    private inventoryService = inject(InventoryService);
    private orderService = inject(OrderService);
    movements: InventoryMovementDto[] = [];//| null = null;
    orderDetail: OrderDto | null = null;
    lstOrderDetail: OrderItemsDto [] = [];
    errorMessage: string | null = null;

    
    balanceFrozen: boolean = false;
    isGeneralVisible: boolean = false; 
    isMonitorVisible: boolean = true; // Set initial state to Monitor (since [value]="1")

    // The dates from your original request:
    private startDate = '2025-10-20';
    private endDate = '2026-07-20';
    // Property to hold the selected order data
    selectedOrder: OrderDto | null = null;// Initialize it to 1 since your HTML starts with [value]="1" (Monitor)
    activeTabIndex: number = 1;

    //Fields Monitor Form
    monitorDropdownTypeDocumentValues = [
        { name: 'PEDIDO', code: 'PD' },
        { name: 'PEDIDO ONLINE', code: 'PDON' }
    ];
    monitorDropdownEstadoValues = [
        { name: 'PENDIENTE', code: 'PENDIENTE' },
        { name: 'CONFIRMADO', code: 'CONFIRMADO' },
        { name: 'DESPACHADO', code: 'DESPACHADO' },
        { name: 'CANCELADO', code: 'CANCELADO' },
        { name: 'ENTREGADO', code: 'ENTREGADO' }
    ];
    monitorDropdownTypeDocumentModel: any = null;
    monitorDropdownEstadoModel: any = null;
    
    monitorFormTypeDocument: String | null = null;
    monitorFormIdCliente: String | null = null;
    monitorFormEstado: String | null = null;
    monitorFormFechaInicio: String | null = null;
    monitorFormNoPedido: String | null = null;
    monitorFormNomCliente: String | null = null;
    monitorFormEstablecimiento: String | null = null;
    monitorFormFechaFinal: String | null = null;

    listOrders: OrderDto[] = [];//| null = null;

    isgeneralDropdownTypeDocumentDisabled: boolean = true;
    isgeneralFormIdClienteDisabled: boolean = true;
    isgeneralDropdownEstadoDisabled: boolean = true;
    isgeneralFormNoPedidoDisabled: boolean = true;

    // Fields General Form 
    generalFormTotalAmount: number | null = null;
    generalFormSubtotal: number | null = null;
    generalDropdownTypeDocumentValues = [
        { name: 'PEDIDO', code: 'PD' },
        { name: 'PEDIDO ONLINE', code: 'PDON' }
    ];
    generalDropdownEstadoValues = [
        { name: 'PENDIENTE', code: 'PENDIENTE' },
        { name: 'CONFIRMADO', code: 'CONFIRMADO' },
        { name: 'DESPACHADO', code: 'DESPACHADO' },
        { name: 'CANCELADO', code: 'CANCELADO' },
        { name: 'ENTREGADO', code: 'ENTREGADO' }
    ];
    generalDropdownTypeDocumentModel: any = null;
    generalDropdownEstadoModel: any = null;

    generalFormIdCliente: String | null = null;
    generalFormNoPedido: String | null = null;
    generalFormImpuesto: String | null = null;

    isgeneralFormTotalAmountDisabled: boolean = true;
    isgeneralFormSubtotalDisabled: boolean = true;
    isgeneralFormImpuestoDisabled: boolean = true;


    // Properties to store the JSON for display
    requestJson: string = '';
    responseJson: string = '';

    // Use a single method triggered by the PrimeNG tab change to switch content
    onTabChange(event: any) {
        const newIndex = event.index;
        
        if (newIndex === 0) { // General Tab
            console.log('General Tab Activated');
            this.isGeneralVisible = true;
            this.isMonitorVisible = false;
            // The form population logic should also go here if needed:
            // this.clearGeneralForm(); 
        } else if (newIndex === 1) { // Monitor Tab
            console.log('Monitor Tab Activated');
            this.isGeneralVisible = false;
            this.isMonitorVisible = true;
        }
    }
    onRowSelect(event: any) {
        console.log('Row Clicked and Selected:');
        // The 'event.data' holds the OrderDto object corresponding to the clicked row
        const clickedOrder: OrderDto = event.data;
        
        // Perform your action here:
        console.log('Row Clicked and Selected:', clickedOrder);
        this.populateGeneralFormValues(clickedOrder);
        this.showGeneralView();
        this.loadOrdersItems();
        
        // Example: Navigate to a detail view or open a modal
        // this.router.navigate(['/order-details', clickedOrder.id]);
    }

    onRowDoubleClick(event: any) {
        const clickedOrder: OrderDto = event.data;
        console.log('Row Double-Clicked:', clickedOrder);
        // Logic to open a form or view details
    }

    @ViewChild('dt') dt!: Table;


    dropdownValues = [
        { name: 'PEDIDO', code: 'PD' },
        { name: 'COTIZACION', code: 'CT' }
    ];
    dropdownEstadoValues = [
        { name: 'PENDIENTE', code: 'PENDIENTE' },
        { name: 'CONFIRMADO', code: 'CONFIRMADO' },
        { name: 'DESPACHADO', code: 'DESPACHADO' },
        { name: 'CANCELADO', code: 'CANCELADO' },
        { name: 'ENTREGADO', code: 'ENTREGADO' }
    ];

    dropdownValue: any = null;
    dropdownEstadoModel: any = null;

    colsToExport : any = [];
    constructor(
        private customerService: CustomerService,
        private productService: ProductService
    ) {}
    ngOnInit() {
        // this.loadMovements();
        this.loadOrders();
        this.showMonitorView();
    }

//   exportSelectedFields() {
//       // Pass the columns to the exportCSV method
//       this.dtableGeneral.exportCSV({ selectionOnly: false, columns: this.colsToExport });
//   }

  ngAfterViewInit() {
    // This hook is called after the component's view has been initialized.
    // This is a safe place to confirm 'dt' is available, although for your
    // export method, the button click itself ensures this.
    console.log('p-table is available:', this.dt);
  }

    exportCSV() {
        console.log("entro a exoport");
        this.dt.exportCSV();
    }
    
    showGeneralView(){
        console.log('showGeneralView selected:');
        this.isGeneralVisible = true;
        this.isMonitorVisible = false;
        this.activeTabIndex = 0; // Set index to 0 (General)
        console.log('TabIndex = ',this.activeTabIndex)
    }
    showMonitorView(){
        console.log('showMonitorView selected:');
        this.isGeneralVisible = false;
        this.isMonitorVisible = true;
        this.activeTabIndex = 1; // Set index to 0 (General)
        console.log('TabIndex = ',this.activeTabIndex)
    }

//   clearTabMonitor(): void {
//     // Logic to clear tabs
//     this.showGeneralView();
//   }
//   clearTabGeneral(): void {
//     // Logic to clear tabs
//     this.showMonitorView();
//   }

    loadMovements(): void {
        this.errorMessage = null;
        this.movements = [];
        console.log(`Getting movements from ${this.startDate} to ${this.endDate}...`);

        this.inventoryService.getMovements(this.startDate, this.endDate).subscribe({
        next: (data) => {
            // Data received is the JSON array (like the one you provided initially)
            this.movements = data; 
            console.log('Movements successfully retrieved movements:', this.movements);
            // console.log('Movements successfully retrieved:', data);
        },
        error: (error) => {
            // Handle errors 
            this.errorMessage = 'Failed to load inventory movements.';
            console.error('API Error:', error);
        }
        });
    }
    loadOrders():void{
         this.listOrders = [];
         this.requestJson = JSON.stringify({ message: "Requesting all orders..." }, null, 2); // Example Request JSON
         this.responseJson = ''; // Clear previous response
         console.log('Getting orders');
          this.orderService.getOrders().subscribe({
            next: (data) => {
                this.listOrders = data; 
                // Capture the response data and format it as a readable JSON string
                this.responseJson = JSON.stringify(data, null, 2);
            },
            error: (error) => {
                this.errorMessage = 'Failed to load orders.';
                console.error('API Error:', error);
                // Capture the error response JSON
                this.responseJson = JSON.stringify(error, null, 2);
            }
          });
    }

    loadOrdersItems():void{
        
         this.orderDetail = null;
         this.requestJson = JSON.stringify({ message: "Requesting all orders..." }, null, 2); // Example Request JSON
         this.responseJson = ''; // Clear previous response
         console.log('Getting orders');
          this.orderService.getOrdersitem("PD002").subscribe({
            next: (data:OrderDto) => {

                this.orderDetail = data;
                // Capture the response 
                // data and format it as a readable JSON string
                this.responseJson = JSON.stringify(data, null, 2);

                this.lstOrderDetail = this.orderDetail.orderItems;
                console.log('getting error',this.lstOrderDetail);
                // console.log(this.lstOrderDetail);
                // this.lstOrderDetail = this.orderDetail[0].orderItems || [];
            },
            error: (error) => {
                this.errorMessage = 'Failed to load orders.';
                console.error('API Error:', error);
                // Capture the error response JSON
                this.responseJson = JSON.stringify(error, null, 2);
            }
          });
    }
    populateGeneralFormValues(clickedOrder: OrderDto):void{
         this.generalFormNoPedido = clickedOrder.id ? clickedOrder.id.toString() : null;
         this.generalFormIdCliente = clickedOrder.customerId ? clickedOrder.customerId.toString() : null;
         this.generalFormTotalAmount = clickedOrder.totalAmount ? clickedOrder.totalAmount.valueOf() : null;
         this.generalFormSubtotal = clickedOrder.totalAmount ? clickedOrder.totalAmount.valueOf() : null;
       

    }
    clearGeneralFormValues():void{
        if (this.generalDropdownTypeDocumentValues.length > 0) {
            this.generalDropdownTypeDocumentModel = this.generalDropdownTypeDocumentValues[0];
        }
        if (this.generalDropdownEstadoValues.length > 0) {
            this.generalDropdownEstadoModel = this.generalDropdownEstadoValues[0];
        }

        this.generalFormIdCliente = null;
        this.generalFormNoPedido = null;
        this.lstOrderDetail = [];
    }
    clearMonitorFormValues():void{
        console.log("clearMonitorFormValues");
        
        
        // Reset Form String Fields (to null or an empty string, depending on your preference)
        this.monitorFormTypeDocument = null; // or ''
        this.monitorFormIdCliente = null; // or ''
        this.monitorFormEstado = null; // or ''
        // this.monitorFormFechaInicio = null; // or ''
        this.monitorFormNoPedido = null; // or ''
        this.monitorFormNomCliente = null; // or ''
        this.monitorFormEstablecimiento = null; // or ''
        this.monitorFormFechaFinal = null; // or ''
        this.listOrders = [];
        // Select the first element of the array
        if (this.monitorDropdownEstadoValues.length > 0) {
            this.monitorDropdownEstadoModel = this.monitorDropdownEstadoValues[0];
        }
        // Select the first element of the array
        if (this.monitorDropdownTypeDocumentValues.length > 0) {
            this.monitorDropdownTypeDocumentModel = this.monitorDropdownTypeDocumentValues[0];
        }
// Option A: Standard local date and time string (e.g., "Wed Oct 25 2023 14:00:00 GMT-0500 (CDT)")
        const dateString = this.monitorFormFechaInicio;
        
        // Option B: Just the date part in 'YYYY-MM-DD' format (useful for APIs)
        // Adjust the time zone offset if needed, but this is a common approach.
        // const dateISOString = this.monitorFormFechaInicio.substring(0, 10);
        
        console.log('Date String:', dateString);
        // return dateISOString;

        // Note: The 'Values' arrays (e.g., monitorDropdownTypeDocumentValues) are left unchanged 
        // as they represent the list of available options, not the selected data.
    }
    ingresarItem():void{
        const orderItemData: OrderItemsDto = {
            id: 'item_6',
            orderId: 'PD002',
            productId: 'product_501',
            inventoryId: 901, 
            quantity: 1, 
            pricePerUnit: 300.99
        };
        this.lstOrderDetail.push(orderItemData);

    }

}
