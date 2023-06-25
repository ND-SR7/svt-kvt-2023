import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Reaction } from '../model/reaction.model';

@Injectable({
  providedIn: 'root'
})
export class ReactionService {

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

    return this.http.get('api/reactions/' + id, queryParams);
  }

  getReactionsForPost(postId: number): Observable<any> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/reactions/post/' + postId, queryParams);
  }

  getReactionsForComment(commentId: number): Observable<any> {
    let queryParams = {};

    queryParams = {
      headers: this.headers,
      observe: 'response'
    };

    return this.http.get('api/reactions/comment/' + commentId, queryParams);
  }

  add(newReaction: Reaction): Observable<any> {
    return this.http.post('api/reactions/add', newReaction, {headers: this.headers, responseType: 'text'});
  }

  delete(id: number): Observable<any> {
    return this.http.delete('api/reactions/delete/' + id, {headers: this.headers})
  }
}
