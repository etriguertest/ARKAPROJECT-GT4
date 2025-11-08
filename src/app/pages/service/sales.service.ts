import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Sale {
    id: number;
    orderId: number | null;
    total: number | null;
    createdAt: Date | null;
    customerId: number | null;
    impuesto: number | null;
    customerName: string | null;
}

export interface SaleItem {
    id: number;
    saleId: number | null;
    productId: number | null;
    quantity: number | null;
    price: number | null;
    productName: string | null;
}

// export interface SalesFilterParams {
//     id?: number; 
//     orderId?: number;
//     customerId?: number;
//     customerName?: string;
//     fromDate?: string; 
//     toDate?: string;
// }


@Injectable({
  providedIn: 'root'
})
export class SalesService {
  private http = inject(HttpClient);
  public baseUrl = 'nocontent';

  /**
   * Calls the GET endpoint to fetch sales with optional filtering.
   * * @param filters The optional query parameters to filter sales.
   * @returns An Observable of the sales array.
   */
  // public getSales(filters: SalesFilterParams = {}): Observable<SaleItem[]> {
  public getSales(filters: HttpParams): Observable<Sale[]> {
    let params = new HttpParams();

    // 2. Build the query string from the filters object
    // Only include parameters that have a value (are not null or undefined)
    for (const [key, value] of Object.entries(filters)) {
      if (value !== undefined && value !== null) {
        // For dates, the value should already be an ISO string (e.g., '2024-01-01T00:00:00Z')
        params = params.set(key, value.toString()); 
      }
    }
    return this.http.get<Sale[]>(
      this.baseUrl + 'api/sales',
      { params: filters }
    );
  }

  /**
   * Calls the GET endpoint to fetch sales with optional filtering.
   * * @param idVenta The optional query parameters
   * @returns An Observable of the sales array.
   */
  public getSaleDetailById(idVenta: string): Observable<SaleItem[]> {
    return this.http.get<SaleItem[]>(this.baseUrl+'api/sales/detail/'+idVenta);
  }

}
