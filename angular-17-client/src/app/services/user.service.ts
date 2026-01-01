import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserInfo } from '../models/user-info';

@Injectable({ providedIn: 'root' })
export class UserService {
  private baseUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  search(orgId: number, q: string, limit = 20): Observable<UserInfo[]> {
    const params = new HttpParams()
      .set('orgId', String(orgId))
      .set('q', q)
      .set('limit', String(limit));

    return this.http.get<UserInfo[]>(`${this.baseUrl}/search`, { params });
  }

  all(orgId: number, limit = 1000): Observable<UserInfo[]> {
    const params = new HttpParams()
      .set('orgId', String(orgId))
      .set('limit', String(limit));

    return this.http.get<UserInfo[]>(`${this.baseUrl}/all`, { params });
  }
}
