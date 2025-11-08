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
import { Sale, SaleItem, SalesService } from '@/pages/service/sales.service';
import { HttpClient, HttpParams } from '@angular/common/http';
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
    templateUrl: `./ventaLayout.html`,
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
export class VentaLayout implements OnInit { 
    constructor(
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
        private configService: ConfigService 
    ) {}
    private salesService = inject(SalesService);
    apiUrl: string |  undefined;
    activeTabIndex: number = 1;
    isGeneralVisible: boolean = false; 
    isMonitorVisible: boolean = true;
    balanceFrozen: boolean = false;

    
    //Fields Monitor
    monitorFormVentaId: number | null = null;
    monitorFormOrderId: string | null = null;
    monitorFormCustomerId: number | null = null;
    monitorFormCustomerName: string | null = null;
    monitorFormFechaInicio: string | null = null;
    monitorFormFechaFinal: string | null = null;
    lstVentas: Sale [] = [];
    selectedVenta: Sale | null = null;

    //Fields General
    generalFormVentaId: string | null = null;
    generalFormOrderId: string | null = null;
    generalFormCustomerId: number | null = null;
    generalFormCustomerName: string | null = null;
    generalFormFechaCreacion: string | null = null;

    generalFormTotalAmount: number | null = null;
    generalFormImpuesto: string | null = null;
    generalFormSubtotal: number | null = null;
    
    lstVentaDetalle: SaleItem[] | [] = [];


    ngOnInit() {
        this.configService.load()
                .then(() => {
                    this.apiUrl = this.configService.get('BASE_URL_SELLING'); 
                    console.log('Successfully loaded config. The API URL is:', this.apiUrl);
                    this.salesService.baseUrl=this.apiUrl ? this.apiUrl:"";
        
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
    }

    clearMonitorFormValues():void{
    }
    loadListVentas(): void {
        this.lstVentas = [];
        let params = new HttpParams()
        .set('id',  this.monitorFormVentaId ? this.monitorFormVentaId : "")
        .set('orderId', this.monitorFormOrderId ? this.monitorFormOrderId : "")
        .set('customerId', this.monitorFormCustomerId ? this.monitorFormCustomerId : "")
        .set('customerName', this.monitorFormCustomerName ? this.monitorFormCustomerName : "")
        // .set('fromDate', "endDate")
        // .set('toDate', "endDate");
        this.salesService.getSales(params).subscribe({
                next: (data:Sale[]) => {
                    // Data received is the JSON array (like the one you provided initially)
                    this.lstVentas = data; 
                    console.log('List Ventas successfully retrieved:',  JSON.stringify(this.lstVentas, null, 2));
                    // console.log('List Pedido successfully retrieved:',  JSON.stringify(data, null, 2));

                    this.messageService.add({
                        severity: 'success',
                        summary: 'Successful',
                        detail: 'Lista de Ventas Cargado',
                        life: 3000
                    });
                    // console.log('Movements successfully retrieved:', data);
                },
                error: (error) => {
                    // Handle errors 
                    this.messageService.add({
                        severity: 'error',
                        summary: 'Successful',
                        detail: 'Ocurrio un problema obteniendo Ventas',
                        life: 3000
                    });
                    console.error('API Error:', error);
                }
        });

    }

    onRowSelect(event: any) {
        console.log('Row Clicked and Selected:');
        // The 'event.data' holds the OrderDto object corresponding to the clicked row
        const clickedVenta: Sale = event.data;
        
        // Perform your action here:
        console.log('Row Clicked and Selected:', clickedVenta);
        this.populateGeneralFormValues(clickedVenta);
        this.showGeneralView();
        this.loadVentaDetalle();
    }
    populateGeneralFormValues(clickedVenta: Sale):void{
        // this.lstPedidoDetalle = clickedVenta.items;

        this.generalFormVentaId = clickedVenta.id ? clickedVenta.id.toString() : null;
        this.generalFormOrderId = clickedVenta.orderId ? clickedVenta.orderId.toString() : null;
        this.generalFormCustomerId = clickedVenta.customerId ? clickedVenta.customerId : null;
        this.generalFormCustomerName = clickedVenta.customerName ? clickedVenta.customerName.toString() : null;
        this.generalFormFechaCreacion = clickedVenta.createdAt ? clickedVenta.createdAt.toString() : null;

        this.generalFormSubtotal = clickedVenta.total ? clickedVenta.total : null;
        this.generalFormImpuesto = clickedVenta.impuesto ? clickedVenta.impuesto.toString() : null;
        if(clickedVenta.impuesto){
            this.generalFormTotalAmount = clickedVenta.total ? clickedVenta.total + clickedVenta.impuesto : null;
        }else{
            this.generalFormTotalAmount = clickedVenta.total ? clickedVenta.total : null;
        }
    
    }

    loadVentaDetalle():void{
         this.lstVentaDetalle = [];
         console.log('Obteniendo detalle venta');
         this.salesService.getSaleDetailById(this.generalFormVentaId!).subscribe({
            next: (data:SaleItem[]) => {
                this.lstVentaDetalle = data;
                console.log('Lista Detalle Venta successfully retrieved:',  JSON.stringify(this.lstVentaDetalle, null, 2));

                this.messageService.add({
                    severity: 'success',
                    summary: 'Successful',
                    detail: 'Lista Detalle de Venta Cargado',
                    life: 3000
                });
            },
            error: (error) => {
                console.error('API Error:', error);
                this.messageService.add({
                    severity: 'error',
                    summary: 'Successful',
                    detail: 'Ocurrio un problema obteniendo Detalle de Venta',
                    life: 3000
                });
            }
          });

    }
        


}