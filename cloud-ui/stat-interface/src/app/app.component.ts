import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { nvD3 } from 'ng2-nvd3';
import { Observable } from 'rxjs';

declare const d3: any;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  entryComponents: [ nvD3 ]
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'app works!';
  constructor() { }
  ngOnInit() { }
  ngOnDestroy() { }
}
