import { Component, OnInit } from '@angular/core';
import { NotificationsWidget } from './components/notificationswidget';
import { StatsWidget } from './components/statswidget';
import { RecentSalesWidget } from './components/recentsaleswidget';
import { BestSellingWidget } from './components/bestsellingwidget';
import { RevenueStreamWidget } from './components/revenuestreamwidget';
import { TopProductWidget } from './arkacomponents/topProductWidget';
import { TopCustomerWidget } from './arkacomponents/topCustomerWidget';
import { ConfigService } from '../service/config.service';
import { DashboardData, SalesService } from '../service/sales.service';
import { PedidoService } from '../service/pedido.service';

@Component({
    selector: 'app-dashboard',
    imports: [StatsWidget,TopProductWidget,TopCustomerWidget],//, RecentSalesWidget, BestSellingWidget, RevenueStreamWidget, NotificationsWidget],
    template: `
        <div class="grid grid-cols-12 gap-8">
            <app-stats-widget [vartotalSales]=vartotalSales 
            [vartotalCarritosAbandonados]=vartotalCarritosAbandonados
            [vartotalOrdenes]=vartotalOrdenes
            class="contents" />
            <div class="col-span-12 xl:col-span-6">

                <app-top-customer-widget [topCustomers]="topCustomers" />
               <!--< <app-recent-sales-widget />
                app-best-selling-widget /> -->
            </div>
            <div class="col-span-12 xl:col-span-6">
                <app-top-products-widget [topProducts]="topProducts" />
                <!--<app-revenue-stream-widget /> 
                <app-notifications-widget />-->
            </div>
        </div>
    `
})
export class Dashboard implements OnInit {
    constructor(
        private configService: ConfigService,
        private salesService: SalesService ,
        private pedidoService: PedidoService 
    ) {}
    apiUrl: string |  undefined;

    vartotalSales = 1705.48;
    vartotalCarritosAbandonados : any = null;
    vartotalOrdenes : any = null;

    topProducts : any[]=[];
    topCustomers: any[]=[]; 
    ngOnInit() {
        this.topProducts = [];
        this.topCustomers = [];
        this.configService.load()
            .then(() => {
                this.apiUrl = this.configService.get('BASE_URL_SELLING'); 
                console.log('Successfully loaded config. The API URL is:', this.apiUrl);
                this.salesService.baseUrl=this.apiUrl ? this.apiUrl:"";
                this.apiUrl = this.configService.get('BASE_URL_ORDERS'); 
                console.log('Successfully loaded config. The API URL is:', this.apiUrl);
                this.pedidoService.baseUrl=this.apiUrl ? this.apiUrl:"";
        
             })
             .catch((error) => {
                 console.error('Application failed to initialize due to config error:', error);
             });
        
        this.salesService.getSalesReport().subscribe({
            next: (data:DashboardData) => {
                this.vartotalSales = data.totalSales;
                this.topProducts = data.topProducts;
                this.topCustomers = data.topCustomers;
                // this.lstPedidos = data.orders; 
                console.log('DashboardData successfully retrieved:',  JSON.stringify(data, null, 2));
            },
            error: (error) => {
                // Handle errors 
                console.error('API Error:', error);
            }
        });

        this.pedidoService.GetTotalNumOrders().subscribe({
            next: (data:any) => {
                // this.lstPedidos = data.orders; 
                console.log('GetTotalNumOrders successfully retrieved:',  JSON.stringify(data, null, 2));
                this.vartotalOrdenes=data;
            },
            error: (error) => {
                // Handle errors 
                console.error('API Error:', error);
            }
        });
        this.pedidoService.GetTotalAbandonCar().subscribe({
            next: (data:any) => {
                // this.lstPedidos = data.orders; 
                console.log('GetTotalNumOrders successfully retrieved:',  JSON.stringify(data, null, 2));
                 this.vartotalCarritosAbandonados =data;
            },
            error: (error) => {
                // Handle errors 
                console.error('API Error:', error);
            }
        });



    }

}
