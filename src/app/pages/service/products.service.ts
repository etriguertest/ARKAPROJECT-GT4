import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';

export interface ProductDto {// Properties correspond directly to the keys in the JSON object
    id: number;
    name: string;
    description: string;
    price: number;
    stock: number;
    categoryName: string;
    categoryId: number;
}



@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private http = inject(HttpClient);
  // NOTE: You must replace '{{path-ms-inventory-local}}' with your actual base URL.
  // Assuming the base part of the URL is correct up to the endpoint path.
  public baseUrl ="";//"https://64474k7pgh.execute-api.us-east-2.amazonaws.com/dev/product/";//'http://18.117.153.32'; // 🚨 Update the base URL

  public getAllProducts(): Observable<ProductDto[]> {
    console.log("BASE RUL ---> ",this.baseUrl);
    return this.http.get<ProductDto[]>(this.baseUrl+'api/products');
    // return of(this.mockProductData);
  }
  public saveProduct(product:ProductDto): Observable<ProductDto> {
    console.log("BASE RUL ---> ",this.baseUrl);
    const requestBody = product;
    console.log("BrequestBody ---> ",requestBody);
    return this.http.post<ProductDto>(this.baseUrl+"api/products", product);
    // return of(this.mockProductData);
  }

  // 📦 Define the mock data array directly in the service
//   private mockProductData: ProductDto[] = [
//       {
//           "id": 2,
//           "name": "Tarjeta de Video NVIDIA RTX 4070",
//           "description": "GPU 12GB GDDR6X para 1440p",
//           "price": 649.00,
//           "stock": 30,
//           "categoryName": "Componentes"
//       },
//       {
//           "id": 1,
//           "name": "Procesador Intel Core i9",
//           "description": "CPU de 14 núcleos para gaming y creación",
//           "price": 599.99,
//           "stock": 50,
//           "categoryName": "Componentes"
//       },
//       {
//           "id": 4,
//           "name": "Mouse Inalámbrico Ergonómico",
//           "description": "Mouse vertical para prevenir fatiga",
//           "price": 79.99,
//           "stock": 120,
//           "categoryName": "Periféricos"
//       },
//       {
//           "id": 3,
//           "name": "Teclado Mecánico RGB",
//           "description": "Teclado con switches Cherry MX Red",
//           "price": 119.50,
//           "stock": 80,
//           "categoryName": "Periféricos"
//       },
//       {
//           "id": 6,
//           "name": "PC Gamer Pre-armada \"Aegis\"",
//           "description": "PC con RTX 4060 y Core i5",
//           "price": 1399.00,
//           "stock": 15,
//           "categoryName": "Computadoras y Laptops"
//       },
//       {
//           "id": 5,
//           "name": "Laptop Dell XPS 15",
//           "description": "Laptop con pantalla OLED 3.5K, Core i7",
//           "price": 1899.99,
//           "stock": 20,
//           "categoryName": "Computadoras y Laptops"
//       },
//       {
//           "id": 8,
//           "name": "Disco Duro Externo 4TB",
//           "description": "Disco externo USB 3.0 para backups",
//           "price": 99.99,
//           "stock": 150,
//           "categoryName": "Almacenamiento"
//       },
//       {
//           "id": 7,
//           "name": "SSD NVMe M.2 2TB",
//           "description": "Disco de estado sólido Gen4 7000MB/s",
//           "price": 149.99,
//           "stock": 200,
//           "categoryName": "Almacenamiento"
//       },
//       {
//           "id": 10,
//           "name": "Switch Ethernet 8 Puertos Gigabit",
//           "description": "Switch no administrable de metal",
//           "price": 29.99,
//           "stock": 90,
//           "categoryName": "Redes y Conectividad"
//       },
//       {
//           "id": 9,
//           "name": "Router WiFi 6 Mesh",
//           "description": "Sistema Mesh de 2 unidades",
//           "price": 229.00,
//           "stock": 60,
//           "categoryName": "Redes y Conectividad"
//       },
//       {
//           "id": 12,
//           "name": "Mouse Pad XXL Gaming",
//           "description": "Alfombrilla extra grande 900x400mm",
//           "price": 24.99,
//           "stock": 250,
//           "categoryName": "Accesorios y Cables"
//       },
//       {
//           "id": 11,
//           "name": "Cable HDMI 2.1 8K",
//           "description": "Cable HDMI de 2 metros alta velocidad",
//           "price": 15.99,
//           "stock": 500,
//           "categoryName": "Accesorios y Cables"
//       },
//       {
//           "id": 15,
//           "name": "Licencia de Windows 11",
//           "description": "Clave de activación",
//           "price": 199.99,
//           "stock": 1000,
//           "categoryName": "Software"
//       },
//       {
//           "id": 14,
//           "name": "Microsoft Office 365 Personal",
//           "description": "Suscripción de 1 año",
//           "price": 69.99,
//           "stock": 1000,
//           "categoryName": "Software"
//       },
//       {
//           "id": 13,
//           "name": "Licencia Windows 11 Pro",
//           "description": "Clave de activación digital",
//           "price": 199.00,
//           "stock": 1000,
//           "categoryName": "Software"
//       }
//   ];
}