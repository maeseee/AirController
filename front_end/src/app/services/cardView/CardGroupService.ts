import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CardGroup} from './CardGroup';

@Injectable({
  providedIn: 'root',
})
export class CardGroupService {
  private http = inject(HttpClient);
  private readonly BASE_URL = 'http://192.168.50.12:9090/cardViews';

  getCardViews(location: 'indoor' | 'outdoor' | 'confidence' | 'statistics'): Observable<CardGroup> {
    const url = `${this.BASE_URL}/${location}`;
    return this.http.get<CardGroup>(url, {withCredentials: true});
  }
}
