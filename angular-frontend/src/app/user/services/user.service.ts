import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';
import { Image } from 'src/app/post/model/image.model';
import { Group } from 'src/app/group/model/group.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private headers = new HttpHeaders({'authorization': 'Bearer ' + JSON.parse(localStorage.user).accessToken,
  'Content-Type': 'application/json'});

  constructor(
    private http: HttpClient
  ) { }

  getOne(id: number): Observable<HttpResponse<User>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/users/' + id, queryParams) as Observable<HttpResponse<User>>;
  }

  getOneByUsername(username: string): Observable<HttpResponse<User>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/users/user/' + username, queryParams) as Observable<HttpResponse<User>>;
  }

  getProfileImage(id: number): Observable<HttpResponse<Image>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/users/' + id + '/image', queryParams) as Observable<HttpResponse<Image>>;
  }

  getUserGroups(userId: number): Observable<HttpResponse<Group[]>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/users/' + userId + '/groups', queryParams) as Observable<HttpResponse<Group[]>>;
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
