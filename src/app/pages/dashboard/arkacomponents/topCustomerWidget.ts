import { Component, Input } from '@angular/core';
import { RippleModule } from 'primeng/ripple';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';
import { Product, ProductService } from '../../service/product.service';
// // Define the interface for a single customer object
// export interface TopCustomer {
//     customerId: number;
//     customerName: string;
//     totalPurchases: number;
// }
@Component({
    standalone: true,
    selector: 'app-top-customer-widget',
    imports: [CommonModule, TableModule, ButtonModule, RippleModule],
    template: `<div class="card mb-8!">
        <div class="font-semibold text-xl mb-4">Top Clientes</div>
        <!--<p-table [value]="topCustomers" [paginator]="true" [rows]="5" responsiveLayout="scroll"> -->
        <p-table [value]="topCustomers" [paginator]="true" [rows]="5" responsiveLayout="scroll">
            <ng-template #header>
                <tr>
                    <th>Id</th>
                    <th pSortableColumn="customerName">Nombre Cliente <p-sortIcon field="customerName"></p-sortIcon></th>
                    <th pSortableColumn="totalPurchases">Total Compras <p-sortIcon field="totalPurchases"></p-sortIcon></th>
                </tr>
            </ng-template>
            <ng-template #body let-topCustomers>
                <tr>
                    <td style="width: 35%; min-width: 7rem;">{{ topCustomers.customerId }}</td>
                    <td style="width: 35%; min-width: 7rem;">{{ topCustomers.customerName }}</td>
                    <td style="width: 35%; min-width: 8rem;">{{ topCustomers.totalPurchases }}</td>
                </tr>
            </ng-template>
        </p-table>
    </div>`,
    providers: [ProductService]
})
export class TopCustomerWidget {
    @Input() topCustomers:any[] = [];
    // products!: Product[];

    // constructor(private productService: ProductService) {}

    // ngOnInit() {
    //     this.productService.getProductsSmall().then((data) => (this.products = data));
    // }
}
