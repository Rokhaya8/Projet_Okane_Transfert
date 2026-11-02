import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe, AsyncPipe, NgIf } from '@angular/common';
import { Sidebar } from '../../../../shared/components/sidebar/sidebar';
import { Header } from '../../../../shared/components/header/header';
import { Observable, timer } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, DatePipe, AsyncPipe, NgIf, Sidebar, Header],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  clock$: Observable<Date> | undefined;

  ngOnInit() {
    this.clock$ = timer(0, 1000).pipe(
      map(() => new Date())
    );
  }
}
