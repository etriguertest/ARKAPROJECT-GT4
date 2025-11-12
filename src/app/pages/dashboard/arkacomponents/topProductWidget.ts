import { Component, Input } from '@angular/core';
import { RippleModule } from 'primeng/ripple';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';
import { Product, ProductService } from '../../service/product.service';

@Component({
    standalone: true,
    selector: 'app-top-products-widget',
    imports: [CommonModule, TableModule, ButtonModule, RippleModule],
    template: `<div class="card mb-8!">
        <div class="font-semibold text-xl mb-4">Top Productos</div>
        <!--<p-table [value]="topProducts" [paginator]="true" [rows]="5" responsiveLayout="scroll"> -->
        <p-table [value]="topProducts" [paginator]="true" [rows]="5" responsiveLayout="scroll">
            <ng-template #header>
                <tr>
                    <th>Id Producto</th>
                    <th pSortableColumn="productName">Nombre <p-sortIcon field="productName"></p-sortIcon></th>
                    <th pSortableColumn="totalQuantity">Productos Adquiridos<p-sortIcon field="totalQuantity"></p-sortIcon></th>
                </tr>
            </ng-template>
            <ng-template #body let-topProducts>
                <tr>
                    <td style="width: 35%; min-width: 7rem;">{{ topProducts.productId }}</td>
                    <td style="width: 35%; min-width: 7rem;">{{ topProducts.productName }}</td>
                    <td style="width: 35%; min-width: 8rem;">{{ topProducts.totalQuantity }}</td>
                </tr>
            </ng-template>
        </p-table>
     </div>`//,
    // providers: [ProductService]
})
export class TopProductWidget {// 1. Define the Input property to receive data from the parent component
    @Input() topProducts:any[] = [];
    // products!: Product[];

    // constructor(private productService: ProductService) {}

    // ngOnInit() {
    //     this.productService.getProductsSmall().then((data) => (this.products = data));
    // }
}
