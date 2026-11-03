import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';

import { CreditComponent } from './pages/credit/credit.component';
import { StatusComponent } from './pages/status/status.component';
import { ArchiveComponent } from './pages/archive/archive.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { DocumentationComponent } from './pages/documentation/documentation.component';

import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent } from './auth/login/login.component';

import { ReclamationFormComponent } from './reclamation/reclamation-form/reclamation-form.component';
import { ChatComponent } from './assistance/chat/chat.component';

import { ClientEspaceComponent } from './client/client-space/client-espace/client-espace.component';


const routes: Routes = [

  // Default
  { path: '', component: HomeComponent },

  { path: 'admin/status', component: StatusComponent },
  { path: 'admin/archives', component: ArchiveComponent },
  { path: 'admin/credits', component: CreditComponent },
  { path: 'admin/documentation', component: DocumentationComponent },
  { path: 'admin/settings', component: SettingsComponent },
  { path: 'admin/home', component: HomeComponent },

  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },

  { path: 'reclamation-form', component: ReclamationFormComponent },
  { path: 'reclamations/:id/chat', component: ChatComponent },
  { path: 'client-espace', component: ClientEspaceComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
