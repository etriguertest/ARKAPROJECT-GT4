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
import { MenubarModule } from 'primeng/menubar';
import { StepperModule } from 'primeng/stepper';
import { TabsModule } from 'primeng/tabs';
import { ButtonGroupModule } from 'primeng/buttongroup';
import { ToolbarModule } from 'primeng/toolbar';
import { InventoryMovement, InventoryService } from '@/pages/service/Inventory.service';
import { OrderDto, OrderItemsDto, OrderService } from '@/pages/service/order.service';
interface expandedRows {
    [key: string]: boolean;
}

@Component({
    selector: 'app-inventory-demo',
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
        ButtonGroupModule
    ],
    templateUrl: `./inventoryLayout.html`,
    styles: `
        .p-datatable-frozen-tbody {
            font-weight: bold;
        }

        .p-datatable-scrollable .p-frozen-column {
            font-weight: bold;
        }
    `,
    providers: [ConfirmationService, MessageService]
})
export class InventoryLayout implements OnInit {
    private inventoryService = inject(InventoryService);
    private orderService = inject(OrderService);
    movements: InventoryMovement[] = [];//| null = null;
    listOrders: OrderDto[] = [];//| null = null;
    orderDetail: OrderDto | null = null;
    lstOrderDetail: OrderItemsDto [] = [];
    errorMessage: string | null = null;

    //FIELDS
    // This is the property that gets updated in onRowSelect
    generalFormTotalAmount: number | null = null;
    generalFormSubtotal: number | null = null;
    
    // Properties to store the JSON for display
    requestJson: string = '';
    responseJson: string = '';

    // The dates from your original request:
    private startDate = '2025-10-20';
    private endDate = '2026-07-20';
        // Property to hold the selected order data
    selectedOrder: OrderDto | null = null;// Initialize it to 1 since your HTML starts with [value]="1" (Monitor)
    activeTabIndex: number = 1;
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
        this.generalFormTotalAmount = clickedOrder.totalAmount ? clickedOrder.totalAmount.valueOf() : null;
        this.generalFormSubtotal = clickedOrder.totalAmount ? clickedOrder.totalAmount.valueOf() : null;
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

    
    balanceFrozen: boolean = false;
    isGeneralVisible: boolean = false; 
    isMonitorVisible: boolean = true; // Set initial state to Monitor (since [value]="1")

    @ViewChild('dt') dt!: Table;


    dropdownValues = [
        { name: 'PEDIDO', code: 'PD' },
        { name: 'COTIZACION', code: 'CT' }
    ];
    dropdownEstadoValues = [
        { name: 'EN TRANSITO', code: 'EN TRANSITO' },
        { name: 'RECIBIDO', code: 'RECIBIDO' },
        { name: 'ENTREGADO', code: 'ENTREGADO' }
    ];

    dropdownValue: any = null;
    dropdownEstadoModel: any = null;

    colsToExport : any = [];
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


}
