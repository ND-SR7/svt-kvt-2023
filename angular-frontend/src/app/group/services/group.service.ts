import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
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

  getOne(id: number): Observable<HttpResponse<Group>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/groups/' + id, queryParams) as Observable<HttpResponse<Group>>;
  }

  getAll(): Observable<HttpResponse<Group[]>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };
    
    return this.http.get('api/groups', queryParams) as Observable<HttpResponse<Group[]>>;
  }

  add(newGroup: Group): Observable<string> {
    return this.http.post('api/groups/add', newGroup, {headers: this.headers, responseType: 'text'});
  }

  edit(editedGroup: Group): Observable<string> {
    return this.http.patch('api/groups/edit/' + editedGroup.id, editedGroup, {headers: this.headers, responseType: 'text'});
  }

  delete(id: number): Observable<HttpResponse<Group>> {
    return this.http.delete('api/groups/delete/' + id, {headers: this.headers}) as Observable<HttpResponse<Group>>;
  }

  deleteGroupAdmin(groupId: number, adminId: number): Observable<HttpResponse<Group>> {
    return this.http.delete('api/groups/delete/' + groupId + '/admin/' + adminId, {headers: this.headers}) as Observable<HttpResponse<Group>>;
  }

  checkUserInGroup(id:number): Observable<HttpResponse<boolean>> {
    return this.http.get('api/posts/group/'+ id + '/user', {headers: this.headers}) as Observable<HttpResponse<boolean>>;
  }
}
