import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms'; // - FormControl represents a single form input with observable valueChanges
import { debounceTime, distinctUntilChanged, switchMap, of, map, tap } from 'rxjs';

import { UserService } from './services/user.service';
import { UserInfo } from './models/user-info';

type Mode = 'REMOTE' | 'LOCAL'; // TypeScript union type used to restrict allowed values

@Component({
  selector: 'app-root', // HTML tag used to render this component
  templateUrl: './app.component.html',// External HTML template
  styleUrls: ['./app.component.css'],// Component-scoped styles
})
export class AppComponent implements OnInit {
  query = new FormControl(''); //Holds the input value, Exposes valueChanges as an Observable (Angular feature)
  mode: Mode = 'REMOTE';
  orgId = 1;

  //State used for LOCAL mode
  localCache: UserInfo[] = [];// Cached users for LOCAL filtering
  localLoaded = false; // Whether cache has been loaded from backend

  //UI state
  suggestions: UserInfo[] = [];//Dropdown suggestion list
  selected?: UserInfo; 

  lastResponseMs: number | null = null;// Used to display elapsed time for the last search operation
  
  //Constructor + Dependency Injection (Angular-specific)
  constructor(private userService: UserService) {}

  //ngOnInit runs once after Angular initializes the component
  //Reactive input pipeline (Angular + RxJS)
  ngOnInit(): void {
    this.query.valueChanges.pipe(//valueChanges is Angular Reactive Forms API, Emits a new value every time the user types
      debounceTime(250),  // Wait 250ms after user stops typing
      distinctUntilChanged(),// Ignore same consecutive values
      switchMap(q => { //Cancels previous HTTP request if user types again， Prevents race conditions (out-of-order responses)
        const text = (q ?? '').trim();

        //Minimum input length guard, Common UX + performance optimization
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

        //Performance timing: Pure browser API.
        const start = performance.now();

        //HttpClient under the hood (inside UserService) Observable returned from HTTP call
        //tap: Side effect only (measure time) Does not alter stream data
        if (this.mode === 'REMOTE') {
          return this.userService.search(this.orgId, text, 20).pipe(
            tap(() => (this.lastResponseMs = performance.now() - start))
          );
        }

        // LOCAL mode (hybrid: HTTP once + in-memory filtering)
        if (!this.localLoaded) {
          return this.userService.all(this.orgId, 50000).pipe(
            tap(list => {
              this.localCache = list; // Cache all users
              this.localLoaded = true;
            }),
            //Then filter locally
            map(() => this.filterLocal(text)),
            //Measure time
            tap(() => (this.lastResponseMs = performance.now() - start))
          );
        }

        const list = this.filterLocal(text);
        this.lastResponseMs = performance.now() - start;
        return of(list);
      })
    ).subscribe(list => { //Subscribe to update UI state（Angular-specific pattern）Angular change detection updates the view automatically
      this.suggestions = list;
    });
  }

  //Mode switch handler (Angular event binding target)
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

  //Select suggestion handler
  choose(u: UserInfo) {
    this.selected = u;
    //emitEvent: false prevents triggering valueChanges
    this.query.setValue(`${u.firstname} ${u.lastname} (${u.email})`, { emitEvent: false });
    this.suggestions = [];
  }

  //Display helper (used in template)
  display(u: UserInfo): string {
    return `${u.firstname} ${u.lastname} (${u.email})`;
  }

  //In-memory filtering
  private filterLocal(q: string): UserInfo[] {
    const s = q.toLowerCase();
    return this.localCache
      .filter(u => (`${u.firstname} ${u.lastname} ${u.email}`).toLowerCase().includes(s))
      .slice(0, 20);
  }

  changeOrg(v: string) {
    this.orgId = Number(v);
    this.selected = undefined;
    this.suggestions = [];
    this.lastResponseMs = null;
  
    // Clears LOCAL cache because cache is org-specific (multi-tenant correctness)
    this.localLoaded = false;
    this.localCache = [];
  
    // Clears input
    this.query.setValue('', { emitEvent: false });
  }
  
}
