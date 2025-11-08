// src/app/core/services/config.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface AppConfig {
  BASE_URL_INVENTORY: string;
  BASE_URL_MOVEMENTS: string;
  BASE_URL_PRODUCS: string;
  BASE_URL_NOTIFICATIONS: string;
  BASE_URL_SELLING: string;
  BASE_URL_SHOPLIST: string;
  BASE_URL_ORDERS: string;
}

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  private config?: AppConfig;

  constructor(private http: HttpClient) {}

  /** Loads the config.json file from the server's root directory. */
  load(): Promise<any> {
    return this.http
      .get<AppConfig>('/assets/config.json')
      .toPromise()
      .then((config) => {
        this.config = config;
      })
      .catch((err) => {
        console.error('Error loading config file', err);
      });
  }

  get(key: keyof AppConfig): string | undefined {
    return this.config?.[key];
  }
}