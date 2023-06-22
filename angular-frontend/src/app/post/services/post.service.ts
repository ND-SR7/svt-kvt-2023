import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Post } from '../model/post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {

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

    return this.http.get('api/posts/' + id, queryParams);
  }

  getImages(id: number): Observable<any> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/posts/' + id + '/images', queryParams);
  }

  getComments(id: number): Observable<any> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/posts/' + id + '/comments', queryParams);
  }

  getAll(): Observable<any> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/posts', queryParams);
  }

  getAllForGroup(id: number): Observable<any> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/posts/group/' + id, queryParams);
  }

  add(newPost: Post): Observable<any> {
    return this.http.post('api/posts/add', newPost, {headers: this.headers, responseType: 'text'});
  }

  edit(editedPost: Post): Observable<any> {
    return this.http.patch('api/posts/edit/' + editedPost.id, editedPost, {headers: this.headers, responseType: 'text'});
  }

  delete(id: number): Observable<any> {
    return this.http.delete('api/posts/delete/' + id, {headers: this.headers})
  }
}
