import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private headers = new HttpHeaders({'authorization': 'Bearer ' + JSON.parse(localStorage.user).accessToken,
  'Content-Type': 'application/json'});

  constructor(
    private http: HttpClient
  ) { }

  getOne(id: number): Observable<any> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/users/' + id, queryParams);
  }

  getOneByUsername(username: string): Observable<any> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/users/user/' + username, queryParams);
  }

  extractUser(): Promise<User> {
    let sub: string;
    const item = localStorage.getItem('user') || "";
    const jwt: JwtHelperService = new JwtHelperService();
    const decodedToken = jwt.decodeToken(item);
    sub = decodedToken.sub;

    return new Promise<User>((resolve, reject) => {
      this.getOneByUsername(sub).subscribe(
        result => {
          const user = result.body as User;
          resolve(user);
        },
        error => {
          reject(error);
        }
      );
    });
  }
}
