import { Component, ElementRef, OnInit, ViewChild ,inject, signal} from '@angular/core';
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
import { DatePickerModule } from 'primeng/datepicker';
import { Pedido, PedidoDetalle, PedidoMessage, PedidoService } from '@/pages/service/pedido.service';
import { ConfigService } from '@/pages/service/config.service';
import { SalesService } from '@/pages/service/sales.service';
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
        ButtonGroupModule,
        DatePickerModule
    ],
    templateUrl: `./pedidoLayout.html`,
    styles: `
        .p-datatable-frozen-tbody {
            font-weight: bold;
        }

        .p-datatable-scrollable .p-frozen-column {
            font-weight: bold;
        }
    `,
    providers: [ConfirmationService, MessageService,ConfigService]
})
export class PedidoLayout implements OnInit { 
    constructor(
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
        private configService: ConfigService 
    ) {}

    private pedidoService = inject(PedidoService);
    private saleService = inject(SalesService);
    apiUrl: string |  undefined;
    activeTabIndex: number = 1;
    isGeneralVisible: boolean = false; 
    isMonitorVisible: boolean = true;
    balanceFrozen: boolean = false;

    //Fields Monitor
    monitorDropdownEstadoValues = [
        { name: 'PENDIENTE', code: 'PENDIENTE' },
        { name: 'CONFIRMADO', code: 'CONFIRMADO' },
        { name: 'EN_DESPACHO', code: 'EN_DESPACHO' },
        { name: 'CANCELADO', code: 'CANCELADO' },
        { name: 'ENTREGADO', code: 'ENTREGADO' }
    ];
    monitorDropdownEstadoModel: any = null;
    lstPedidos: Pedido [] = [];
    selectedPedido: Pedido | null = null;

    //Fields General
    generalFormOrderId: string | null = null;
    generalFormCustomer: string | null = null;
    generalFormStatus: string | null = null;
    generalFormOrderDate: string | null = null;
    generalFormConfirmationDate: string | null = null;

    lstPedidoDetalle: PedidoDetalle [] = [];

    generalFormTotalAmount: number | null = null;
    generalFormImpuesto: string | null = null;
    generalFormSubtotal: number | null = null;

    ngOnInit() {
        this.configService.load()
                .then(() => {
                    this.apiUrl = this.configService.get('BASE_URL_ORDERS'); 
                    console.log('Successfully loaded config. The API URL is:', this.apiUrl);
                    this.pedidoService.baseUrl=this.apiUrl ? this.apiUrl:"";
                    this.apiUrl = this.configService.get('BASE_URL_SELLING'); 
                    console.log('Successfully loaded config. The API URL is:', this.apiUrl);
                    this.saleService.baseUrl=this.apiUrl ? this.apiUrl:"";
        
                })
                .catch((error) => {
                    console.error('Application failed to initialize due to config error:', error);
                });
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

    onTabChange(event: any) {
        const newIndex = event.index;
    }

    clearGeneralFormValues():void{
        this.generalFormOrderId = ""; 
        this.generalFormCustomer = "";
        this.generalFormStatus= "";
        this.generalFormOrderDate= "";
        this.generalFormConfirmationDate= "";

        this.generalFormTotalAmount = null;
        this.generalFormImpuesto = null;
        this.generalFormSubtotal = null;
        this.lstPedidoDetalle =[];
    }

    confirmacionPedido():void{
        console.log("Confirmacion de Pedido -> ",this.generalFormOrderId);
         this.saleService.confirmSale(this.generalFormOrderId!).subscribe({
                next: (data:string) => {
                    console.log('List Pedido successfully retrieved:',  JSON.stringify(data, null, 2));
                    this.messageService.add({
                        severity: 'success',
                        summary: 'Successful',
                        detail: 'Confirmacion de Pedido Exitosa',
                        life: 3000
                    });
                },
                error: (error) => {
                    // Handle errors 
                    this.messageService.add({
                        severity: 'error',
                        summary: 'Successful',
                        detail: 'Ocurrio un problema confirmando el pedido',
                        life: 3000
                    });
                    console.error('API Error:', error);
                }
                });
            this.clearGeneralFormValues();
    }

    loadListPedidos(): void {
        this.lstPedidos = [];
        let estado = ""; 
        if (this.monitorDropdownEstadoModel != null) {
            estado = this.monitorDropdownEstadoModel.name; 
            console.log("if (this.monitorDropdownEstadoModel != null)")
        }
         this.pedidoService.GetOrderByStatus(estado).subscribe({
                next: (data:PedidoMessage) => {
                    // Data received is the JSON array (like the one you provided initially)
                    this.lstPedidos = data.orders; 
                    console.log('List Pedido successfully retrieved:',  JSON.stringify(this.lstPedidos, null, 2));
                    // console.log('List Pedido successfully retrieved:',  JSON.stringify(data, null, 2));

                    this.messageService.add({
                        severity: 'success',
                        summary: 'Successful',
                        detail: 'Lista de Pedidos Cargado',
                        life: 3000
                    });
                    // console.log('Movements successfully retrieved:', data);
                },
                error: (error) => {
                    // Handle errors 
                    this.messageService.add({
                        severity: 'error',
                        summary: 'Successful',
                        detail: 'Ocurrio un problema obteniendo Pedidos',
                        life: 3000
                    });
                    console.error('API Error:', error);
                }
                });

    }

    clearMonitorFormValues():void{
    }

    onRowSelect(event: any) {
        console.log('Row Clicked and Selected:');
        // The 'event.data' holds the OrderDto object corresponding to the clicked row
        const clickedOrder: Pedido = event.data;
        
        // Perform your action here:
        console.log('Row Clicked and Selected:', clickedOrder);
        this.populateGeneralFormValues(clickedOrder);
        this.showGeneralView();
        // this.loadPedidoDetalle();
    }

        populateGeneralFormValues(clickedOrder: Pedido):void{
            this.lstPedidoDetalle = clickedOrder.items;

            this.generalFormOrderId = clickedOrder.idOrder ? clickedOrder.idOrder.toString() : null;
            this.generalFormCustomer = clickedOrder.customerId ? clickedOrder.customerId.toString() : null;
            this.generalFormStatus = clickedOrder.status ? clickedOrder.status.toString() : null;
            this.generalFormOrderDate = clickedOrder.orderDate ? clickedOrder.orderDate.toString() : null;
            this.generalFormConfirmationDate = clickedOrder.confirmationDate ? clickedOrder.confirmationDate.toString() : null;

            this.generalFormTotalAmount = clickedOrder.totalAmount ? clickedOrder.totalAmount : null;
            // this.generalFormImpuesto = clickedOrder.idOrder ? clickedOrder.idOrder.toString() : null;
            this.generalFormSubtotal = clickedOrder.totalAmount ? clickedOrder.totalAmount : null;
           
    
        }
        
}