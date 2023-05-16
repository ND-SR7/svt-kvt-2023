import { Routes } from '@angular/router';

import { GroupListComponent } from '../group/group-list/group-list.component';
import { LoginComponent } from '../user/login/login.component';
import { RegisterComponent } from '../user/register/register.component';

export const routes :Routes = [
	{path: '/api/groups', component: GroupListComponent},
	{path: '/api/users/login', component: LoginComponent},
	{path: '/api/users/signup', component: RegisterComponent}
];
