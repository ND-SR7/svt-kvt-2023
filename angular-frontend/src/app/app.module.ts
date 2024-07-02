import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
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
import { AddEditPostComponent } from './post/add-edit-post/add-edit-post.component';
import { AddEditGroupComponent } from './group/add-edit-group/add-edit-group.component';
import { FriendsComponent } from './user/friends/friends.component';
import { EditUserComponent } from './user/edit-user/edit-user.component';
import { FriendRequestsComponent } from './user/friend-requests/friend-requests.component';
import { GroupRequestsComponent } from './group/group-requests/group-requests.component';
import { ToastrModule } from 'ngx-toastr';
import { SearchPostComponent } from './post/search-post/search-post.component';
import { SearchGroupComponent } from './group/search-group/search-group.component';

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
    AddEditPostComponent,
    AddEditGroupComponent,
    FriendsComponent,
    EditUserComponent,
    FriendRequestsComponent,
    GroupRequestsComponent,
    SearchPostComponent,
    SearchGroupComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
