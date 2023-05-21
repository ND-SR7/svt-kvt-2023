import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing/app-routing.module';
import { GroupComponent } from './group/group.component';
import { LoginComponent } from './user/login/login.component';
import { GroupListComponent } from './group/group-list/group-list.component';
import { UserComponent } from './user/user.component';
import { RegisterComponent } from './user/register/register.component';
import { PostComponent } from './post/post.component';
import { HttpClientModule } from '@angular/common/http';
import { NavbarComponent } from './core/navbar/navbar.component';
import { ChangePasswordComponent } from './user/change-password/change-password.component';
import { PostListComponent } from './post/post-list/post-list.component';
import { EditPostComponent } from './post/edit-post/edit-post.component';
import { EditGroupComponent } from './group/edit-group/edit-group.component';

@NgModule({
  declarations: [
    AppComponent,
    GroupComponent,
    LoginComponent,
    GroupListComponent,
    UserComponent,
    RegisterComponent,
    PostComponent,
    NavbarComponent,
    ChangePasswordComponent,
    PostListComponent,
    EditPostComponent,
    EditGroupComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
