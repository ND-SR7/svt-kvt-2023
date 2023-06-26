import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Post } from '../model/post.model';
import { Image } from '../model/image.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  private headers = new HttpHeaders({'authorization': 'Bearer ' + JSON.parse(localStorage.user).accessToken,
  'Content-Type': 'application/json'});

  constructor(
    private http: HttpClient
  ) { }

  getOne(id: number): Observable<HttpResponse<Post>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/posts/' + id, queryParams) as Observable<HttpResponse<Post>>;
  }

  getImages(id: number): Observable<HttpResponse<Image[]>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/posts/' + id + '/images', queryParams) as Observable<HttpResponse<Image[]>>;
  }

  getComments(id: number): Observable<HttpResponse<Comment[]>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/posts/' + id + '/comments', queryParams) as Observable<HttpResponse<Comment[]>>;
  }

  getAll(): Observable<HttpResponse<Post[]>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/posts', queryParams) as Observable<HttpResponse<Post[]>>;
  }

  getAllForGroup(id: number): Observable<HttpResponse<Post[]>> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/posts/group/' + id, queryParams) as Observable<HttpResponse<Post[]>>;
  }

  add(newPost: Post): Observable<string> {
    return this.http.post('api/posts/add', newPost, {headers: this.headers, responseType: 'text'});
  }

  edit(editedPost: Post): Observable<string> {
    return this.http.patch('api/posts/edit/' + editedPost.id, editedPost, {headers: this.headers, responseType: 'text'});
  }

  delete(id: number): Observable<HttpResponse<Post>> {
    return this.http.delete('api/posts/delete/' + id, {headers: this.headers}) as Observable<HttpResponse<Post>>;
  }
}
