import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-top-right-component',
  templateUrl: './top-right-component.component.html',
  styleUrls: ['./top-right-component.component.css']
})
export class TopRightComponentComponent implements OnInit {
  value = 50;
  options;
  constructor() {
  }

  ngOnInit() {
    this.options = {
      displayPrevious: true,
      barCap: 25,
      trackWidth: 30,
      barWidth: 20,
      trackColor: 'rgba(255,0,0,.1)',
      prevBarColor: 'rgba(0,0,0,.2)',
      textColor: 'rgba(255,0,0,.6)'
    };
  }

}
