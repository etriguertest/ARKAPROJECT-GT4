import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define the shape of a single inventory movement object
export interface PedidoMessage {
    success: boolean;
    message: number;
    orders: Pedido[] | []; 
}
export interface Pedido {
    idOrder: string;
    customerId: number;
    status: string;
    orderDate: string; 
    confirmationDate: string | null; 
    totalAmount: number; 
    items: PedidoDetalle[] | []; 
}
export interface PedidoDetalle {
    productId: string;
    productName: string;
    quantity: number;
    price: number; 
}

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private http = inject(HttpClient);
  public baseUrl = 'nocontent';
  
    public GetOrderByStatus(status:string): Observable<PedidoMessage> {
        return this.http.get<PedidoMessage>(this.baseUrl+'api/order/order-status/'+status);
    }

    public GetTotalNumOrders(): Observable<string> {
        return this.http.get<any>(this.baseUrl+'api/order/notabandoned/count');
    }
  
    public GetTotalAbandonCar(): Observable<string> {
        return this.http.get<any>(this.baseUrl+'api/order/abandoned/count');
    }
  
  

}
