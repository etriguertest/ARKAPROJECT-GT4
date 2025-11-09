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
import { InventoryDto, InventoryItemUpdateQuantities, InventoryMovementDto, InventoryService, WarehouseBranch } from '@/pages/service/Inventory.service';
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
    templateUrl: `./inventoryLayout.html`,
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
export class InventoryLayout implements OnInit { 
    constructor(
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
                private configService: ConfigService 
    ) {}
    private inventoryService = inject(InventoryService);
    apiUrl: string |  undefined;
    rowItemInventoryelectedItem: number = 0;
   
    //Variables de Tab
    // balanceFrozen: boolean = false;
    isGeneralVisible: boolean = false; 
    isMonitorVisible: boolean = true; // Set initial state to Monitor (since [value]="1")
    activeTabIndex: number = 1;
    balanceFrozen: boolean = false;

    //Fields Monitor Form
    warehouseBranchs = signal<WarehouseBranch[]>([]);
    lstMovements: InventoryMovementDto [] = [];
    lstInventory: InventoryDto [] = [];
    monitorDropdownBranchSelected: string = '';
    monitorDropdownBranchValues : { id: number | string; name: string }[] = [];
    monitorDropdownBranchModel: any = null;
    monitorDropdownEstadoValues = [
        { name: 'IN STOCK', code: 'IN STOCK' },
        { name: 'LOWSTOCK', code: 'LOWSTOCK' },
        { name: 'OUTOFSTOCK', code: 'OUTOFSTOCK' }
    ];
    monitorDropdownEstadoModel: any = null;
    
    monitorFormIdBranch: string | null = null;
    monitorFormEstado: string | null = null;
    monitorFormFechaInicio: string | null = null;
    monitorFormFechaFinal: string | null = null;
    
    selectedInventory: InventoryDto | null = null;// Initialize it to 1 since your HTML starts with [value]="1" (Monitor)
    lstInventoryItemUpdateQuantities: InventoryItemUpdateQuantities[] = [];

    // Fields General Form 
    generalFormProducto: string | null = null;
    generalFormSucursal: string | null = null;
    generalDropdownEstadoValues = [
        { name: 'IN STOCK', code: 'IN STOCK' },
        { name: 'LOWSTOCK', code: 'LOWSTOCK' },
        { name: 'OUTOFSTOCK', code: 'OUTOFSTOCK' }
    ];
    generalDropdownEstadoModel: any = null;

    // isgeneralDropdownEstadoDisabled: boolean = true;
    isRowAbleToBeSelected: boolean =false;

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
    onSelectionChange(event: any) {
        // This is guaranteed to run *after* a selection is made.
        console.log("Selected Value is now:", this.generalDropdownEstadoModel);
        // Or access the value directly from the event:
        // console.log("Selected Value is now:", event.value);
    }
    onRowSelect(event: any) {
        // console.log('Row Event Index:',event);
        // The 'event.data' holds the OrderDto object corresponding to the clicked row
        const clickedInventory: InventoryDto = event.data;

        // **THIS IS THE SOLUTION:** Find the index of the clicked object in your array
        const rowIndex: number = this.lstInventory.findIndex(
            item => item === clickedInventory
        );
        this.rowItemInventoryelectedItem = rowIndex;
        //set Estado al estado seleccionado
        const inventoryStatus = this.lstInventory[rowIndex].status;
        const inStockOption = this.generalDropdownEstadoValues.find(
                (option: any) => option.name === inventoryStatus
            );
        if (inStockOption) {
            this.generalDropdownEstadoModel = inStockOption;
        }
        // Perform your action here:
        console.log('Row Clicked and Selected:', clickedInventory);
        console.log('Row INDEX:', rowIndex); 
        this.showGeneralView();
        if (clickedInventory.id !== null) {
            this.loadLastMovements(clickedInventory.id);
        } else {
            console.warn("Cannot load movements: Inventory ID is null.");
        }
        
        // Example: Navigate to a detail view or open a modal
        // this.router.navigate(['/order-details', clickedOrder.id]);
    }

    onRowDoubleClick(event: any) {
        const clickedInventory: InventoryDto = event.data;
        console.log('Row Double-Clicked:', clickedInventory);
        // Logic to open a form or view details
    }

    onQuantityChange(event: any, item: any, index: number,antes :number) {
        console.log("ITEM --> ",item);
        console.log("ANTES ---> DESPUES-->",antes," ",item);
        // Get the new value from the input field
        const newQuantity = event.target.value ? parseInt(event.target.value, 10) : 0;

        // Update the quantity property of the specific item in your array
        // It's generally safer to update a copy or ensure change detection runs.
        this.lstInventory[index].quantity = newQuantity;
        this.lstInventoryItemUpdateQuantities = [];
        this.lstInventoryItemUpdateQuantities.push({
            numOrder:null,
            type:2,
            productId:this.lstInventory[index].productId,
            quantity:this.lstInventory[index].quantity,
            
        });
        console.log('List Inventory about to send:',  JSON.stringify(this.lstInventoryItemUpdateQuantities, null, 2));
        this.inventoryService.adjustStockInventory(this.lstInventoryItemUpdateQuantities).subscribe({
        next: (data:string) => {
            console.log('List Inventory successfully retrieved:',  JSON.stringify(data, null, 2));
            this.messageService.add({
                severity: 'success',
                summary: 'Successful',
                detail: 'Ajuste de Inventario exitoso',
                life: 3000
            });
            // console.log('Movements successfully retrieved:', data);
        },
        error: (error) => {
            // Handle errors 
            this.messageService.add({
                severity: 'error',
                summary: 'Successful',
                detail: 'Ocurrio un problema ajustando Inventario',
                life: 3000
            });
            console.error('API Error:', error);
        }
        });
        
        // If you are using OnPush change detection, you might need an extra step
        // to force detection, like spreading the array:
        // this.lstInventory = [...this.lstInventory]; 
        
        console.log(`Updated item at index ${index} to new quantity: ${newQuantity}`);
    }

    ngOnInit() {
        this.configService.load()
        .then(() => {
            this.apiUrl = this.configService.get('BASE_URL_INVENTORY'); 
            console.log('Successfully loaded config. The API URL is:', this.apiUrl);
            this.inventoryService.baseUrl=this.apiUrl ? this.apiUrl:"";
            this.apiUrl = this.configService.get('BASE_URL_MOVEMENTS'); 
            console.log('Successfully loaded config. The API URL is:', this.apiUrl);
            this.inventoryService.baseUrlMovements=this.apiUrl ? this.apiUrl:"";
            this.inventoryService.getAllBranchs().subscribe((data:WarehouseBranch[]) => {
                this.warehouseBranchs.set(data);
                // CONVERSION STEP: Map the data to the desired DropdownItem structure
                // **CORRECTION:** Use the assignment operator `=` instead of `:` followed by `[]`
                this.monitorDropdownBranchValues = data.map((branch: WarehouseBranch) => ({
                        id: branch.id, 
                        name: branch.branchName 
                }));
                // 3. Set the signal for the dropdown values
                // this.monitorDropdownEstadoValues.push(convertedData);
            });

        })
        .catch((error) => {
            console.error('Application failed to initialize due to config error:', error);
        });
        // this.loadMovements();
        this.loadInventoryItems();
        this.showMonitorView();
    }
    actionBackwards():void{
        console.log("rowItemInventoryelectedItem --> ",this.rowItemInventoryelectedItem);
        this.selectedInventory = this.lstInventory[this.rowItemInventoryelectedItem-1];
        console.log("selectedInventory --> ",this.selectedInventory);
    }

    actionFordward():void{
        console.log("rowItemInventoryelectedItem --> ",this.rowItemInventoryelectedItem);
        this.selectedInventory = this.lstInventory[this.rowItemInventoryelectedItem+11];
        console.log("selectedInventory --> ",this.selectedInventory);

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

    loadInventoryItems(): void {
    }


    clearGeneralFormValues():void{
        this.monitorFormIdBranch = null; // or ''
        this.monitorFormEstado = null; // or '
        if (this.monitorDropdownEstadoValues.length > 0) {
            this.monitorDropdownEstadoModel = this.monitorDropdownEstadoValues[0];
        }
    }

    getFormattedDate(dateValue: string | number | Date ): string {
        const dateObj = new Date(dateValue);
        
        // Safety check for invalid dates
        if (isNaN(dateObj.getTime())) {
            return ''; 
        }
        
        const year = dateObj.getFullYear();
        const month = (dateObj.getMonth() + 1).toString().padStart(2, '0'); 
        const day = dateObj.getDate().toString().padStart(2, '0');
        
        return `${year}-${month}-${day}`;
    }

    clearMonitorFormValues():void{
    
    
    }
    updateStockShowInput():void{
        this.isRowAbleToBeSelected = true;

    }
    getStockSeverity(stock: string): string {
        if (stock=="IN STOCK") {
            // Green for high stock
            return 'success'; 
        } else if (stock=="LOWSTOCK") {
            // Yellow/Orange for medium stock
            return 'warning';
        } else {
            // Red for low stock (10 or less)
            return 'danger'; 
        }
    }

    private startDate = '2024-08-15';
    private endDate = '2025-12-20';
    loadListInventoriesProducts(): void {
        console.log("loadListInventoriesProducts () --->",this.monitorDropdownEstadoModel);
        this.lstInventory = [];
        this.isRowAbleToBeSelected = false;
        console.log(`Getting movements from ${this.startDate} to ${this.endDate}...`);
        let estado = ""; 
        if (this.monitorDropdownEstadoModel != null) {
            estado = this.monitorDropdownEstadoModel.name; 
            console.log("if (this.monitorDropdownEstadoModel != null)")
        }
        if (this.monitorFormFechaInicio) {
            this.startDate=this.getFormattedDate(this.monitorFormFechaInicio);
        }
        if (this.monitorFormFechaFinal) {
            this.endDate=this.getFormattedDate(this.monitorFormFechaFinal);
        }

        if (this.monitorDropdownBranchModel != null) {
            this.monitorDropdownBranchSelected = this.monitorDropdownBranchModel.id; 
            console.log("if (this.monitorDropdownEstadoModel != null)")
        }
        this.inventoryService.getInventoryByFilters(this.startDate, this.endDate,estado,this.monitorDropdownBranchSelected).subscribe({
        next: (data:InventoryDto[]) => {
            // Data received is the JSON array (like the one you provided initially)
            this.lstInventory = data; 
            console.log('List Inventory successfully retrieved:',  JSON.stringify(this.lstInventory, null, 2));
            this.messageService.add({
                severity: 'success',
                summary: 'Successful',
                detail: 'Lista de Invetorio Cargado',
                life: 3000
            });
            // console.log('Movements successfully retrieved:', data);
        },
        error: (error) => {
            // Handle errors 
            this.messageService.add({
                severity: 'error',
                summary: 'Successful',
                detail: 'Ocurrio un problema obteniendo Inventario',
                life: 3000
            });
            console.error('API Error:', error);
        }
        });
    }
    loadLastMovements(inventoryUnitId :number): void {
        this.lstMovements = [];
        console.log(`Getting las 10 movements from ${inventoryUnitId}`);

        this.inventoryService.getLast10Movements(inventoryUnitId).subscribe({
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
