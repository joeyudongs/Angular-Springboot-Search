import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, of, map, tap } from 'rxjs';

import { UserService } from './services/user.service';
import { UserInfo } from './models/user-info';

type Mode = 'REMOTE' | 'LOCAL';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  query = new FormControl('');
  mode: Mode = 'REMOTE';
  orgId = 1;

  localCache: UserInfo[] = [];
  localLoaded = false;

  suggestions: UserInfo[] = [];
  selected?: UserInfo;

  lastResponseMs: number | null = null;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.query.valueChanges.pipe(
      debounceTime(250),
      distinctUntilChanged(),
      switchMap(q => {
        const text = (q ?? '').trim();
        if (text.length < 2) {  // min length
          this.suggestions = [];
          this.lastResponseMs = null;
          return of<UserInfo[]>([]);
        }

        if (!text) {
          this.suggestions = [];
          this.lastResponseMs = null;
          return of<UserInfo[]>([]);
        }

        const start = performance.now();

        if (this.mode === 'REMOTE') {
          return this.userService.search(this.orgId, text, 20).pipe(
            tap(() => (this.lastResponseMs = performance.now() - start))
          );
        }

        // LOCAL mode
        if (!this.localLoaded) {
          return this.userService.all(this.orgId, 1000).pipe(
            tap(list => {
              this.localCache = list;
              this.localLoaded = true;
            }),
            map(() => this.filterLocal(text)),
            tap(() => (this.lastResponseMs = performance.now() - start))
          );
        }

        const list = this.filterLocal(text);
        this.lastResponseMs = performance.now() - start;
        return of(list);
      })
    ).subscribe(list => {
      this.suggestions = list;
    });
  }

  setMode(m: Mode) {
    this.mode = m;
    this.selected = undefined;
    this.suggestions = [];
    this.lastResponseMs = null;

    if (m === 'LOCAL') {
      this.localLoaded = false;
      this.localCache = [];
    }
  }

  choose(u: UserInfo) {
    this.selected = u;
    this.query.setValue(`${u.firstname} ${u.lastname} (${u.email})`, { emitEvent: false });
    this.suggestions = [];
  }

  display(u: UserInfo): string {
    return `${u.firstname} ${u.lastname} (${u.email})`;
  }

  private filterLocal(q: string): UserInfo[] {
    const s = q.toLowerCase();
    return this.localCache
      .filter(u => (`${u.firstname} ${u.lastname} ${u.email} ${u.uname}`).toLowerCase().includes(s))
      .slice(0, 20);
  }

  changeOrg(v: string) {
    this.orgId = Number(v);
    this.selected = undefined;
    this.suggestions = [];
    this.lastResponseMs = null;
  
    // org 改变后，local cache 必须清空（因为 cache 只属于某个 org）
    this.localLoaded = false;
    this.localCache = [];
  
    // 可选：清空输入框
    this.query.setValue('', { emitEvent: false });
  }
  
}
