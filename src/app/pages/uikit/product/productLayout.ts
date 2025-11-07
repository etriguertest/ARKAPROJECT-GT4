
import { Component, OnInit, signal, ViewChild ,inject} from '@angular/core';
import { Table, TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';
import { ToastModule } from 'primeng/toast';
import { ToolbarModule } from 'primeng/toolbar';
import { RatingModule } from 'primeng/rating';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { SelectModule } from 'primeng/select';
import { RadioButtonModule } from 'primeng/radiobutton';
import { InputNumberModule } from 'primeng/inputnumber';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { InputIconModule } from 'primeng/inputicon';
import { IconFieldModule } from 'primeng/iconfield';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
// import { Product, ProductService } from '@/pages/service/product.service';
import { ProductDto, ProductService } from '@/pages/service/products.service';
import { ConfigService } from '@/pages/service/config.service';
import { DropdownItem, InventorySaveSingleItemRequestDto, InventoryService, WarehouseBranch } from '@/pages/service/Inventory.service';
import { switchMap } from 'rxjs/operators';

interface Column {
    field: string;
    header: string;
    customExportHeader?: string;
}

interface ExportColumn {
    title: string;
    dataKey: string;
}

@Component({
    selector: 'app-product-demo',
    standalone: true,
    imports: [
        CommonModule,
        TableModule,
        FormsModule,
        ButtonModule,
        RippleModule,
        ToastModule,
        ToolbarModule,
        RatingModule,
        InputTextModule,
        TextareaModule,
        SelectModule,
        RadioButtonModule,
        InputNumberModule,
        DialogModule,
        TagModule,
        InputIconModule,
        IconFieldModule,
        ConfirmDialogModule
    ],
    templateUrl: `./productLayout.html`,
    // styles: `
    //     .p-datatable-frozen-tbody {
    //         font-weight: bold;
    //     }

    //     .p-datatable-scrollable .p-frozen-column {
    //         font-weight: bold;
    //     }
    // `,
    providers: [ConfirmationService, ProductService, MessageService,ConfigService,InventoryService]
})

export class ProductLayout implements OnInit {
    productDialog: boolean = false;
    apiUrl: string |  undefined;

    products = signal<ProductDto[]>([]);
    warehouseBranchs = signal<WarehouseBranch[]>([]);
    // products: ProductDto [] = [];

    // product!: ProductDto | null = null;
    // product: ProductDto | [] = [];
    product: ProductDto | null = null;
    inventoryItemReq: InventorySaveSingleItemRequestDto | null = null;

    selectedProducts!: ProductDto[] | null;

    submitted: boolean = false;

    statuses!: any[];

    @ViewChild('dt') dt!: Table;

    exportColumns!: ExportColumn[];

    cols!: Column[];

    constructor(
        private productService: ProductService,
        private inventoryService: InventoryService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
        private configService: ConfigService 
    ) {}


    //Form Create
    branchFormModel: string | null = null;
    costFormModel: string | null = null;

    monitorDropdownEstadoValues : { id: number | string; name: string }[] = [];
    monitorDropdownEstadoModel: any = null;
    

    exportCSV() {
        this.dt.exportCSV();
    }

    ngOnInit() {
        this.loadDemoData();
    }

    loadDemoData() {
        this.configService.load()
        .then(() => {
            this.apiUrl = this.configService.get('BASE_URL_PRODUCS'); 
            console.log('Successfully loaded config. The BASE_URL_PRODUCS URL is:', this.apiUrl);
            this.productService.baseUrl=this.apiUrl ? this.apiUrl:"";
            console.log('Successfully loaded config. The BASE_URL_PRODUCS URL is:', this.productService.baseUrl);
            this.productService.getAllProducts().subscribe((data:ProductDto[]) => {
                this.products.set(data);
            });

            this.apiUrl = this.configService.get('BASE_URL_INVENTORY'); 
            console.log('Successfully loaded config. The BASE_URL_INVENTORY URL is:', this.apiUrl);
            this.inventoryService.baseUrl=this.apiUrl ? this.apiUrl:"";
            console.log('Successfully loaded config. The BASE_URL_INVENTORY URL is:', this.inventoryService.baseUrl);

            this.inventoryService.getAllBranchs().subscribe((data:WarehouseBranch[]) => {
                this.warehouseBranchs.set(data);
                // CONVERSION STEP: Map the data to the desired DropdownItem structure
                // **CORRECTION:** Use the assignment operator `=` instead of `:` followed by `[]`
                this.monitorDropdownEstadoValues = data.map((branch: WarehouseBranch) => ({
                        id: branch.id, 
                        name: branch.branchName 
                }));
                // 3. Set the signal for the dropdown values
                // this.monitorDropdownEstadoValues.push(convertedData);
            });
            console.log("warehouseBranchs--->",this.warehouseBranchs);


            this.messageService.add({
                severity: 'success',
                summary: 'Successful',
                detail: 'Lista de Productos Cargado',
                life: 3000
            });
            this.productService.getAllProducts().subscribe((data:ProductDto[]) => {
                this.products.set(data);
            });

        })
        .catch((error) => {
            console.error('Application failed to initialize due to config error:', error);
            this.messageService.add({
                severity: 'error',
                summary: 'Successful',
                detail: 'Ocurrio un problema con carga de lista de productos',
                life: 3000
            });
        });

        this.statuses = [
            { label: 'INSTOCK', value: 'instock' },
            { label: 'LOWSTOCK', value: 'lowstock' },
            { label: 'OUTOFSTOCK', value: 'outofstock' }
        ];

        this.cols = [
            { field: 'id', header: 'Id', customExportHeader: 'Product Code' },
            { field: 'name', header: 'Name' },
            { field: 'price', header: 'Price' },
            { field: 'categoryName', header: 'Categoria' }
        ];

        this.exportColumns = this.cols.map((col) => ({ title: col.header, dataKey: col.field }));
    }

    onGlobalFilter(table: Table, event: Event) {
        table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
    }

    openNew() {
        // this.product = {};
        this.product = {} as ProductDto;
        this.submitted = false;
        this.productDialog = true;
    }

    editProduct(product: ProductDto) {
            console.log("JSON STRING ",JSON.stringify(product, null, 2));
        this.product = { ...product };
        this.productDialog = true;
    }

    deleteSelectedProducts() {
        this.confirmationService.confirm({
            message: 'Are you sure you want to delete the selected products?',
            header: 'Confirm',
            icon: 'pi pi-exclamation-triangle',
            accept: () => {
                this.products.set(this.products().filter((val) => !this.selectedProducts?.includes(val)));
                this.selectedProducts = null;
                this.messageService.add({
                    severity: 'success',
                    summary: 'Successful',
                    detail: 'Products Deleted',
                    life: 3000
                });
            }
        });
    }

    hideDialog() {
        this.productDialog = false;
        this.submitted = false;
    }

    // deleteProduct(product: ProductDto) {
    //     this.confirmationService.confirm({
    //         message: 'Are you sure you want to delete ' + product.name + '?',
    //         header: 'Confirm',
    //         icon: 'pi pi-exclamation-triangle',
    //         accept: () => {
    //             this.products.set(this.products().filter((val) => val.id !== product.id));
    //             // this.product = {};
    //             this.messageService.add({
    //                 severity: 'success',
    //                 summary: 'Successful',
    //                 detail: 'Product Deleted',
    //                 life: 3000
    //             });
    //         }
    //     });
    // }

    findIndexById(id: number): number {
        let index = -1;
        for (let i = 0; i < this.products().length; i++) {
            if (this.products()[i].id === id) {
                index = i;
                break;
            }
        }

        return index;
    }

    createId(): string {
        let id = '';
        var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        for (var i = 0; i < 5; i++) {
            id += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return id;
    }

    getSeverity(status: string) {
        
        switch (status) {
            case 'INSTOCK':
                return 'success';
            case 'LOWSTOCK':
                return 'warn';
            case 'OUTOFSTOCK':
                return 'danger';
            default:
                return 'info';
        }
    }

    saveProduct() {
        this.submitted = true;
        let _products = this.products();
        if (this.product!.name?.trim()) {
            if (this.product!.id) {
                _products[this.findIndexById(this.product!.id)] = this.product!;
                this.products.set([..._products]);
                this.messageService.add({
                    severity: 'success',
                    summary: 'Successful',
                    detail: 'Product Updated',
                    life: 3000
                });
            } else {
                if (!this.product){// || !this.inventoryItemReq) {
                    this.messageService.add({
                        severity: 'error',
                        summary: 'Error',
                        detail: 'Product or Inventory data is missing.',
                        life: 3000
                    });
                    return; // Stop execution if data is missing
                }
                // this.product!.id = 0;//this.createId();
                // this.product.image = 'product-placeholder.svg';
                this.products.set([..._products, this.product!]);
                
                // console.log("JSON STRING ",JSON.stringify(this.product, null, 2));
                // this.productService.saveProduct(this.product!).subscribe((data:ProductDto) => {
                //     // this.products.set(data);
                //     this.inventoryItemReq!.quantity=data.stock;
                //     this.inventoryItemReq!.branchId=this.product!.categoryId;
                //     this.inventoryItemReq!.productId=data.id;
                //     this.inventoryItemReq!.acquisitionCost=data.price;

                //     this.inventoryService.saveInventory(this.inventoryItemReq!).subscribe((data:string) => {
                //         this.messageService.add({
                //             severity: 'success',
                //             summary: 'Successful',
                //             detail: 'Product Created',
                //             life: 3000
                //         });
                //     });
                // });
                // Start the Observable Chain

                this.inventoryItemReq = {} as InventorySaveSingleItemRequestDto;
                let estado: number;
                if (this.monitorDropdownEstadoModel != null) {
                    estado = this.monitorDropdownEstadoModel.id; 
                    console.log("if (this.monitorDropdownEstadoModel != null)")
                }
                this.productService.saveProduct(this.product).pipe(
                    
                    // 1. switchMap waits for saveProduct to complete successfully.
                    // The result (ProductDto) is passed as 'productData'.
                    switchMap((productData: ProductDto) => {
                    
                    // 2. DATA MAPPING (This is where your inner logic goes)
                    // Transfer required fields from the successful ProductDto response 
                    // to the pending Inventory Request object.
                    this.inventoryItemReq!.quantity = productData.stock; // Use stock as quantity
                    this.inventoryItemReq!.branchId = estado; // Use the *local* categoryId
                    this.inventoryItemReq!.productId = productData.id; // CRITICAL: Use the new ID from the server
                    this.inventoryItemReq!.acquisitionCost = +this.costFormModel!;
                    
                    console.log(`Product saved (ID: ${productData.id}). Now saving inventory.`);
                    
                    console.log(`this.inventoryItemReq: ${this.inventoryItemReq}).`);
                    // 3. Return the new observable (the inventory save call)
                    return this.inventoryService.saveInventory(this.inventoryItemReq!);
                    })
                ).subscribe({
                    // 4. 'next' executes ONLY after the ENTIRE chain is successful.
                    next: (inventorySaveResponse: string) => {
                    this.messageService.add({
                        severity: 'success',
                        summary: 'Successful',
                        detail: 'Product Created and Inventory Updated',
                        life: 3000
                    });
                    // Handle post-save logic (e.g., clearing form, updating view)
                    },
                    // 5. 'error' catches failure from EITHER API call.
                    error: (err) => {
                    console.error('An error occurred during the save process:', err);
                    this.messageService.add({
                        severity: 'error',
                        summary: 'Error',
                        detail: 'Failed to complete product or inventory save.',
                        life: 5000
                    });
                    }
                });

            }

            this.productDialog = false;
            this.product = null;
        }
    }

    // your-component.component.ts

// ... other code and imports

    getStockSeverity(stock: number): string {
        if (stock > 50) {
            // Green for high stock
            return 'success'; 
        } else if (stock > 5) {
            // Yellow/Orange for medium stock
            return 'warning';
        } else {
            // Red for low stock (10 or less)
            return 'danger'; 
        }
    }
    getLabelSeverity(stock:number):string{
        if (stock > 50) {
            // Green for high stock
           return "IN STOCK";
        } else if (stock > 5) {
            // Yellow/Orange for medium stock
            return "LOWSTOCK";
        } else {
            // Red for low stock (10 or less)
            return "OUTOFSTOCK"; 
        }
    }
}
