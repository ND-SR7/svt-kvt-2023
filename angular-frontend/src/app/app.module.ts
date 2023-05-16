import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing/app-routing.module';
import { GroupComponent } from './group/group.component';
import { LoginComponent } from './user/login/login.component';
import { GroupListComponent } from './group/group-list/group-list.component';
import { UserComponent } from './user/user.component';
import { RegisterComponent } from './user/register/register.component';
import { PostComponent } from './post/post.component';

@NgModule({
  declarations: [
    AppComponent,
    GroupComponent,
    LoginComponent,
    GroupListComponent,
    UserComponent,
    RegisterComponent,
    PostComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
