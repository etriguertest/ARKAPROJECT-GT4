import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define the shape of a single inventory movement object
export interface OrderDto {
    id: string;
    // LocalDateTime in Java is typically represented as a string (ISO 8601 format) 
    // or Date object in TypeScript/JavaScript.
    orderDate: string; 
    customerId: number; // Long in Java
    status: string;
    // BigDecimal in Java is typically represented as a number or string in TypeScript
    totalAmount: number; 
    // The list of order items DTOs
    orderItems: OrderItemsDto[] | []; 
}
export interface OrderItemsDto {
    id: string;
    orderId: string;
    productId: string;
    inventoryId: number; // Long in Java
    quantity: number; // int in Java
    // BigDecimal in Java is typically represented as a number or string in TypeScript
    pricePerUnit: number; 
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private http = inject(HttpClient);
  // NOTE: You must replace '{{path-ms-inventory-local}}' with your actual base URL.
  // Assuming the base part of the URL is correct up to the endpoint path.
  private baseUrl = 'http://localhost:8082'; // 🚨 Update the base URL

  /**
   * Retrieves a range of inventory movements from the server.
   * @param startDate The start date for the range (e.g., '2025-10-20').
   * @param endDate The end date for the range (e.g., '2026-07-20').
   * @returns An Observable that resolves to an array of InventoryMovement objects.
   */
  public getOrders(): Observable<OrderDto[]> {
    // 1. Set up the query parameters
    // let params = new HttpParams()
    //   .set('start', startDate)
    //   .set('end', endDate);

    // 2. Perform the GET request
    // The resulting URL will be: 
    // {{baseUrl}}?start=startDate&end=endDate
    return this.http.get<OrderDto[]>(this.baseUrl+'/api/v1/orders');
  }

  public getOrdersitem(orderId: string): Observable<OrderDto> {
    // 1. Set up the query parameters
    // let params = new HttpParams()
    //   .set('start', startDate)
    //   .set('end', endDate);

    // 2. Perform the GET request
    // The resulting URL will be: 
    // {{baseUrl}}?start=startDate&end=endDate
    // return this.http.get<OrderDto[]>(this.baseUrl+'/api/v1/orders/'+orderId);
    return this.http.get<OrderDto>(this.baseUrl+'/api/v1/orders/'+orderId);
  }
}