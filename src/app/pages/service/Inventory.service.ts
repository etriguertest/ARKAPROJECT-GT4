import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define the shape of a single inventory movement object
export interface InventoryMovementDto {
  id: number| null;
  inventoryUnitId: number;
  productName: string;
  movementType: string;
  movementDate: string;
  documentReference: string;
  idOperator: number;
  nameOperator: string;
  quantityType: 'DECREMENT' | 'INCREMENT';
  quantity: number| null;
  fromBranch: number| null;
  toBranch: number | null;
}

export interface InventoryDto {
  id: number| null;
  productId: number;
  dateReceived: string;
  manufactureDate: string;
  warrantyEndDate: string;
  status: number;
  warehouseLocation: string;
  acquisitionCost: 'DECREMENT' | 'INCREMENT';
  quantity: number| null;
  branchId: number| null;
  branchName: string | null;
}
export interface InventorySaveSingleItemRequestDto {
  productId: number;
  acquisitionCost: number;
  quantity: number ;
  branchId: number ;
}

export interface WarehouseBranch {
  id: number;
  branchName: string;
  address: string;
  city: string;
  provinceState: string;
  country: string;
  phone: string;
  email: string;
  managerName: string;
  dateOpened: string;
  warehouseType: string;
  active: boolean;
}
export interface InventoryItemUpdateQuantities {
    numOrder: number | null;
    type: 0 | 1 | 2; 
    productId: number;
    quantity: number;
}

// Define the expected dropdown item structure
export interface DropdownItem {
    id: string | number; // Use the correct type for your ID
    name: string;
}

// ... inside your co

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private http = inject(HttpClient);
  public baseUrl = "nocontent";
  public baseUrlMovements = "nocontent";

  /**
   * Retrieves a range of inventory movements from the server.
   * @param startDate The start date for the range (e.g., '2025-10-20').
   * @param endDate The end date for the range (e.g., '2026-07-20').
   * @returns An Observable that resolves to an array of InventoryMovement objects.
   */
  public getMovements(params:HttpParams): Observable<InventoryMovementDto[]> {
    // 1. Set up the query parameters
    // let params = new HttpParams()
    //   .set('start', startDate)
    //   .set('end', endDate);

    // 2. Perform the GET request
    // The resulting URL will be: 
    // {{baseUrl}}?start=startDate&end=endDate
    return this.http.get<InventoryMovementDto[]>(this.baseUrl+"api/v1/inventory-movements/search", { params: params });
  }

  /**
   * Retrieves top10 inventory movements from the server.
   * @param inventoryUnitId The unitId Inventory
   * @returns An Observable that resolves to an array of InventoryMovement objects.
   */
  public getLast10Movements(inventoryUnitId :number): Observable<InventoryMovementDto[]> {
    return this.http.get<InventoryMovementDto[]>(this.baseUrlMovements+"api/v1/inventory-movements/last-movements/"+inventoryUnitId);
  }

  /**
   * Retrieves a range of inventory movements from the server.
   * @param startDate The start date for the range (e.g., '2025-10-20').
   * @param endDate The end date for the range (e.g., '2026-07-20').
   * @param status The  status for Inventory
   * @param branchId The branchId status for Inventory
   * @returns An Observable that resolves to an array of InventoryDto objects.
   */
  public getInventoryByFilters(startDate: string, endDate: string,status: string,branchId:string): Observable<InventoryDto[]> {
    const requestBody = {
        starDate: startDate,
        finishDate: endDate,
        status: status,
        branchId: branchId
    };
    return this.http.post<InventoryDto[]>(this.baseUrl+"api/v1/inventory/search-by-filters", requestBody);
  
  }

  /**
   * Retrieves top10 inventory movements from the server.
   * @param inventory The InventorySaveSingleItemRequestDto para guardar inventario
   * @returns An Observable that resolves to an array of string objects.
   */
  public saveInventory(inventory:InventorySaveSingleItemRequestDto): Observable<string> {
    return this.http.post<string>(this.baseUrl+"api/v1/inventory", inventory);
  
  }

  /**
   * Retrieves top10 inventory movements from the server.
   * @param inventoryUnitId The unitId Inventory
   * @returns An Observable that resolves to an array of WarehouseBranch objects.
   */
  public getAllBranchs(): Observable<WarehouseBranch[]> {
    return this.http.get<WarehouseBranch[]>(this.baseUrl+"api/v1/branch");
  }

  public adjustStockInventory(adijustStockQuantities: InventoryItemUpdateQuantities[]): Observable<string> {
   
    return this.http.post<string>(this.baseUrl+"api/v1/inventory/update-quantities", adijustStockQuantities);
  
  }

}