import { provideHttpClient, withFetch } from '@angular/common/http';
// import { ApplicationConfig ,inject , APP_INITIALIZER} from '@angular/core';
import { ApplicationConfig } from '@angular/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter, withEnabledBlockingInitialNavigation, withInMemoryScrolling } from '@angular/router';
import Aura from '@primeuix/themes/aura';
import { providePrimeNG } from 'primeng/config';
import { appRoutes } from './app.routes';
import { ConfigService } from '@/pages/service/config.service';
// /**
//  * Factory function that injects the ConfigService and returns a function
//  * that calls the load method.
//  * This runs before the application bootstraps.
//  */
// function initializeApp() {
//   const configService = inject(ConfigService);
//   // Return the function that loads the configuration
//   return () => configService.load();
// }
export const appConfig: ApplicationConfig = {
    providers: [
        provideRouter(appRoutes, withInMemoryScrolling({ anchorScrolling: 'enabled', scrollPositionRestoration: 'enabled' }), withEnabledBlockingInitialNavigation()),
        provideHttpClient(withFetch()),
        provideAnimationsAsync(),
        // 🚀 Add the Application Initializer Provider HERE 🚀
        // {
        //     provide: APP_INITIALIZER,
        //     useFactory: initializeApp,
        //     deps: [ConfigService], // Explicitly declare the service dependency
        //     multi: true // Essential for allowing multiple initializers
        // },
        providePrimeNG({ theme: { preset: Aura, options: { darkModeSelector: '.app-dark' } } })
    ]
};
