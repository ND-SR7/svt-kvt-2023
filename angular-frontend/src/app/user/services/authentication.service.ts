import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private headers = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(private http: HttpClient) {}

  	login(auth: any): Observable<any> {
		return this.http.post('api/users/login', {username: auth.username, password: auth.password}, {headers: this.headers, responseType: 'json'});
	}

	logout(): Observable<any> {
		const authorizedHeaders = new HttpHeaders({'authorization': 'Bearer ' + JSON.parse(localStorage.user).accessToken, 'Content-Type': 'application/json'})
		return this.http.get('api/users/logout', {headers: authorizedHeaders, responseType: 'text'});
	}	

	register(auth: any): Observable<any> {
		return this.http.post('/api/users/signup', 
			{username: auth.username, password: auth.password, email: auth.email, firstName: auth.firstName, lastName: auth.lastName}, 
			{headers: this.headers, responseType: 'json'});
	}

	changePassword(auth: any): Observable<any> {
		const authorizedHeaders = new HttpHeaders({'authorization': 'Bearer ' + JSON.parse(localStorage.user).accessToken, 'Content-Type': 'application/json'})
		return this.http.post('api/users/change-password', {oldPassword: auth.oldPassword, newPassword: auth.newPassword}, {headers: authorizedHeaders, responseType: 'json'});
	}

	isLoggedIn(): boolean {
		if (!localStorage.getItem('user')) {
			return false;
		}
		return true;
	}
}
