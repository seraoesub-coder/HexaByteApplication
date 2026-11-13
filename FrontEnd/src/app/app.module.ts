import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// dashboard components
import { LayoutComponent } from './dashboard/layout/layout.component';
import { TopBarComponent } from './dashboard/top-bar/top-bar.component';
import { OverlayComponent } from './dashboard/overlay/overlay.component';
import { SidebarComponent } from './dashboard/sidebar/sidebar/sidebar.component';
import { SidebarItemComponent } from './dashboard/sidebar/sidebar-item/sidebar-item.component';
import { SidebarItemsComponent } from './dashboard/sidebar/sidebar-items/sidebar-items.component';
import { SidebarHeaderComponent } from './dashboard/sidebar/sidebar-header/sidebar-header.component';

// pages
import { HomeComponent } from './pages/home/home.component';
import { CreditComponent } from './pages/credit/credit.component';
import { StatusComponent } from './pages/status/status.component';
import { ArchiveComponent } from './pages/archive/archive.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { DocumentationComponent } from './pages/documentation/documentation.component';


// others
import { DocComponent } from './components/docs/doc/doc.component';
import { ContentComponent } from './components/content/content.component';
import { SnippetComponent } from './components/docs/snippet/snippet.component';
import { FolderIconComponent } from './components/docs/icons/folder-icon/folder-icon.component';

import { FormsModule } from "@angular/forms";

import { HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';

import { NgOptimizedImage } from "@angular/common";


import { ReclamPageComponent } from './reclam-page/reclam-page.component';
import { HeaderComponent } from './layout/header/header.component';
import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent } from './auth/login/login.component';
import { ReclamationFormComponent } from './reclamation/reclamation-form/reclamation-form.component';
import { authInterceptor } from './core/interceptors/auth.interceptor';

// icons
import { HomeIconComponent } from './dashboard/icons/home-icon/home-icon.component';
import { CreditIconComponent } from './dashboard/icons/credit-icon/credit-icon.component';
import { StatusIconComponent } from './dashboard/icons/status-icon/status-icon.component';
import { ArchiveIconComponent } from './dashboard/icons/archive-icon/archive-icon.component';
import { SettingsIconComponent } from './dashboard/icons/settings-icon/settings-icon.component';
import { DocIconComponent } from './dashboard/icons/doc-icon/doc-icon.component';
import { ClientEspaceComponent } from './client-espace/client-espace/client-espace.component';
import { RendezVousListComponent } from './rendez-vous/rendez-vous-list/rendez-vous-list.component';



@NgModule({
  declarations: [

    // Main Component
    AppComponent,

    // dashboard
    LayoutComponent,
    TopBarComponent,
    OverlayComponent,
    SidebarComponent,
    SidebarItemComponent,
    SidebarItemsComponent,
    SidebarHeaderComponent,

    // pages
    HomeComponent,
    CreditComponent,
    StatusComponent,
    ArchiveComponent,
    SettingsComponent,
    DocumentationComponent,


    // icons
    DocIconComponent,
    HomeIconComponent,
    ArchiveIconComponent,
    CreditIconComponent,
    StatusIconComponent,
    SettingsIconComponent,


    // others
    DocComponent,
    SnippetComponent,
    ContentComponent,
    FolderIconComponent,

    ReclamPageComponent,
    HeaderComponent,
    RegisterComponent,
    LoginComponent,
    ClientEspaceComponent,
    RendezVousListComponent,



  ],
  imports: [BrowserModule, AppRoutingModule, FormsModule, NgOptimizedImage, ReclamationFormComponent ],
  providers: [provideHttpClient(withInterceptors([authInterceptor]))],
  bootstrap: [AppComponent],
})
export class AppModule {}
