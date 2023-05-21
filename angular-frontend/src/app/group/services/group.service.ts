import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Group } from '../model/group.model';

@Injectable({
  providedIn: 'root'
})
export class GroupService {

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

    return this.http.get('api/groups/' + id, queryParams);
  }

  getAll(): Observable<any> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/groups', queryParams);
  }

  add(newGroup: Group): Observable<any> {
    return this.http.post('api/groups/add', newGroup, {headers: this.headers, responseType: 'text'});
  }

  edit(editedGroup: Group): Observable<any> {
    return this.http.patch('api/groups/edit/' + editedGroup._id, editedGroup, {headers: this.headers, responseType: 'text'});
  }

  delete(id: number): Observable<any> {
    return this.http.delete('api/groups/delete/' + id, {headers: this.headers})
  }
}
