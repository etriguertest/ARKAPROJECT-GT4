import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    standalone: true,
    selector: 'app-stats-widget',
    imports: [CommonModule],
    template: `<div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Ordenes</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">---</div>
                    </div>
                    <div class="flex items-center justify-center bg-blue-100 dark:bg-blue-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-truck text-blue-500 text-xl!"></i>
                    </div>
                </div>
                <!--<span class="text-primary font-medium">24 new </span>
                <span class="text-muted-color">since last visit</span> -->
                <br>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Ventas</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">\${{vartotalSales}}</div>
                    </div>
                    <div class="flex items-center justify-center bg-orange-100 dark:bg-orange-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-dollar text-orange-500 text-xl!"></i>
                    </div>
                </div>
                <!--<span class="text-primary font-medium">%52+ </span> -->
                <span class="text-muted-color">Ultima Semana</span>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Clientes</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">---</div>
                    </div>
                    <div class="flex items-center justify-center bg-cyan-100 dark:bg-cyan-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-users text-cyan-500 text-xl!"></i>
                    </div>
                </div>
               <!-- <span class="text-primary font-medium">520 </span>
                <span class="text-muted-color">newly registered</span> -->
                <br>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Carritos Abandonados</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">---</div>
                    </div>
                    <div class="flex items-center justify-center bg-purple-100 dark:bg-purple-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-shopping-cart text-purple-500 text-xl!"></i>
                    </div>
                </div>
                <!--<span class="text-primary font-medium">85 </span>
                <span class="text-muted-color">responded</span> -->
                <br>
            </div>
        </div>`
})
export class StatsWidget {
    @Input() vartotalSales:number = 0;
}
