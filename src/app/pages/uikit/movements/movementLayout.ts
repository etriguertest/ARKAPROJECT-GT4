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
import { DatePickerModule } from 'primeng/datepicker';
import { InventoryDto, InventoryMovementDto, InventoryService } from '@/pages/service/Inventory.service';
import { ConfigService } from '@/pages/service/config.service';
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
    templateUrl: `./movementLayout.html`,
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
export class MovementLayout implements OnInit { 
    constructor(
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
        private configService: ConfigService // 👈 Inject the service
    ) {}
    private inventoryService = inject(InventoryService);
    apiUrl: string |  undefined;
    //Variables de Tab
    // balanceFrozen: boolean = false;
    isMonitorVisible: boolean = true; // Set initial state to Monitor (since [value]="1")
    activeTabIndex: number = 1;
    balanceFrozen: boolean = false;

    //Fields Monitor Form
    lstMovements: InventoryMovementDto [] = [];
    monitorDropdownEstadoValues = [
        { name: 'ENTRADA POR COMPRA', code: 'ENTRADA_POR_COMPRA' },
        { name: 'VENTA DESPACHO CLIENTE', code: 'VENTA_DESPACHO_CLIENTE' }
    ];
    monitorDropdownEstadoModel: any = null;
    
    monitorFormDocReference: String | null = null;
    monitorFormIdInventory: String | null = null;
    monitorFormIdBranch: String | null = null;
    monitorFormEstado: String | null = null;
    monitorFormFechaInicio: String | null = null;
    monitorFormFechaFinal: String | null = null;
    
    selectedInventory: InventoryDto | null = null;// Initialize it to 1 since your HTML starts with [value]="1" (Monitor)
    
    // Fields General Form 
    isgeneralDropdownEstadoDisabled: boolean = true;

    // Use a single method triggered by the PrimeNG tab change to switch content
    onTabChange(event: any) {
        const newIndex = event.index;
        
        if (newIndex === 0) { // General Tab
            console.log('General Tab Activated');
            this.isMonitorVisible = false;
            // The form population logic should also go here if needed:
            // this.clearGeneralForm(); 
        } else if (newIndex === 1) { // Monitor Tab
            console.log('Monitor Tab Activated');
            this.isMonitorVisible = true;
        }
    }

    onRowSelect(event: any) {
        console.log('Row Clicked and Selected:');
        // The 'event.data' holds the OrderDto object corresponding to the clicked row
        const clickedInventory: InventoryDto = event.data;
        
        // Perform your action here:
        console.log('Row Clicked and Selected:', clickedInventory);
        // this.populateGeneralFormValues(clickedInventory);
        
        // Example: Navigate to a detail view or open a modal
        // this.router.navigate(['/order-details', clickedOrder.id]);
    }
    onRowDoubleClick(event: any) {
        const clickedInventory: InventoryDto = event.data;
        console.log('Row Double-Clicked:', clickedInventory);
        // Logic to open a form or view details
    }


    ngOnInit() {
        this.configService.load()
        .then(() => {
            this.apiUrl = this.configService.get('BASE_URL_MOVEMENTS'); 
            console.log('Successfully loaded config. The API URL is:', this.apiUrl);
            this.inventoryService.baseUrl=this.apiUrl ? this.apiUrl:"";

        })
        .catch((error) => {
            console.error('Application failed to initialize due to config error:', error);
        });
        // this.loadMovements();
        this.loadInventoryItems();
        this.showMonitorView();
    }

    showMonitorView(){
        console.log('showMonitorView selected:');
        this.isMonitorVisible = true;
        this.activeTabIndex = 1; // Set index to 0 (General)
        console.log('TabIndex = ',this.activeTabIndex)
    }

    loadInventoryItems(): void {
    }


    clearGeneralFormValues():void{
    }

    clearMonitorFormValues():void{
        this.monitorFormDocReference= null; // or ''
        this.monitorFormIdInventory = null; // or ''
        this.monitorFormIdBranch = null; // or ''
        this.monitorFormEstado = null; // or '
        if (this.monitorDropdownEstadoValues.length > 0) {
            this.monitorDropdownEstadoModel = this.monitorDropdownEstadoValues[0];
        }
    
    
    }

    private startDate = '2024-08-15';
    private endDate = '2025-12-20';
    // loadListInventoriesProducts(): void {
    //     this.lstInventory = [];
    //     console.log(`Getting movements from ${this.startDate} to ${this.endDate}...`);

    //     this.inventoryService.getInventoryByFilters(this.startDate, this.endDate,"","").subscribe({
    //     next: (data:InventoryDto[]) => {
    //         // Data received is the JSON array (like the one you provided initially)
    //         this.lstInventory = data; 
    //         console.log('List Inventory successfully retrieved:',  JSON.stringify(this.lstInventory, null, 2));
    //         this.messageService.add({
    //             severity: 'success',
    //             summary: 'Successful',
    //             detail: 'Lista de Invetorio Cargado',
    //             life: 3000
    //         });
    //         // console.log('Movements successfully retrieved:', data);
    //     },
    //     error: (error) => {
    //         // Handle errors 
    //         this.messageService.add({
    //             severity: 'danger',
    //             summary: 'Successful',
    //             detail: 'Ocurrio un problema obteniendo Inventario',
    //             life: 3000
    //         });
    //         console.error('API Error:', error);
    //     }
    //     });
    // }
    loadMovements(): void {
        this.lstMovements = [];
        console.log(`Getting movements from ${this.startDate} to ${this.endDate}...`);

        this.inventoryService.getMovements(this.startDate, this.endDate).subscribe({
        next: (data:InventoryMovementDto[]) => {
            // Data received is the JSON array (like the one you provided initially)
            this.lstMovements = data; 
            console.log('Lista Movimiento de Inventory successfully retrieved:',  JSON.stringify(this.lstMovements, null, 2));
            this.messageService.add({
                severity: 'success',
                summary: 'Successful',
                detail: 'Lista de Invetorio Cargado',
                life: 3000
            });
        },
        error: (error) => {
            // Handle errors 
            this.messageService.add({
                severity: 'error',
                summary: 'Successful',
                detail: 'Ocurrio un problema obteniendo Movimientos de Inventario',
                life: 3000
            });
            console.error('API Error:', error);
        }
        });
    }


}
