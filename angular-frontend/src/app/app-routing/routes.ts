import { Routes } from '@angular/router';

import { GroupListComponent } from '../group/group-list/group-list.component';
import { LoginComponent } from '../user/login/login.component';
import { RegisterComponent } from '../user/register/register.component';
import { ChangePasswordComponent } from '../user/change-password/change-password.component';
import { GroupComponent } from '../group/group.component';
import { PostListComponent } from '../post/post-list/post-list.component';
import { PostComponent } from '../post/post.component';
import { UserComponent } from '../user/user.component';
import { AddEditPostComponent } from '../post/add-edit-post/add-edit-post.component';
import { AddEditGroupComponent } from '../group/add-edit-group/add-edit-group.component';

export const routes :Routes = [
	{path: 'posts', component: PostListComponent, title: 'Social Network'},
	{path: 'posts/add', component: AddEditPostComponent, title: 'Social Network'},
	{path: 'posts/:id', component: PostComponent, title: 'Social Network'},
	{path: 'posts/edit/:id', component: AddEditPostComponent, title: 'Social Network'},
	{path: 'groups', component: GroupListComponent, title: 'Social Network'},
	{path: 'groups/add', component: AddEditGroupComponent, title: 'Social Network'},
	{path: 'groups/:id', component: GroupComponent, title: 'Social Network'},
	{path: 'groups/:id/add-post', component: AddEditPostComponent, title: 'Social Network'},
	{path: 'groups/edit/:id', component: AddEditGroupComponent, title: 'Social Network'},
	{path: 'users/profile', component: UserComponent, title: 'Social Network'},
	{path: 'users/login', component: LoginComponent, title: 'Social Network'},
	{path: 'users/register', component: RegisterComponent, title: 'Social Network'},
	{path: 'users/change-password', component: ChangePasswordComponent, title: 'Social Network'},
	{path: '', pathMatch: 'full', redirectTo: 'users/login', title: 'Social Network'}
];
