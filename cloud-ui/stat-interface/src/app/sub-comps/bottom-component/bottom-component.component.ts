import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { nvD3 } from 'ng2-nvd3';
import { Observable } from 'rxjs';
//import * as d3 from 'd3';
declare const d3: any;

@Component({
  selector: 'app-bottom-component',
  templateUrl: './bottom-component.component.html',
  styleUrls: ['./bottom-component.component.css'],
  entryComponents: [ nvD3 ]
})
export class BottomComponentComponent { // implements OnInit, OnDestroy {
//  options;
//  data = [];
//  subscription;
//
//  @ViewChild(nvD3)
//  nvD3: nvD3;
//
//  ngOnInit() {
//    const timer = Observable.timer(1000, 500);
//    this.subscription = timer.subscribe(t => this.update(t));
//    this.options = {
//      chart: {
//        type: 'lineChart',
//        height: 230,
//        margin: {
//          top: 20,
//          right: 20,
//          bottom: 40,
//          left: 55
//        },
//        x: function(d) { return d.x; },
//        y: function(d) { return d.y; },
//        useInteractiveGuideline: true,
//        dispatch: {
//          stateChange: function(e) { console.log('stateChange'); },
//          changeState: function(e) { console.log('changeState'); },
//          tooltipShow: function(e) { console.log('tooltipShow'); },
//          tooltipHide: function(e) { console.log('tooltipHide'); }
//        },
//        xAxis: {
//          axisLabel: 'Response Number'
//        },
//        yAxis: {
//          axisLabel: 'Response Time',
//          tickFormat: function(d) {
//            return d3.format('.02f')(d);
//          },
//          axisLabelDistance: -10
//        },
//        callback: function(chart) {
//          console.log('!!! lineChart callback !!!');
//        }
//      }
//    };
//    this.data = randomNums();
//  }
//
//  update(x: any) {
//    this.data[0].values.push({x: x + 100, y: getRandomInt(3, 90)});
//    this.data[0].values.shift();
//    this.nvD3.chart.update();
//  }
//
//  ngOnDestroy() {
//    this.subscription.unsubscribe();
//  }
//}
//
//function sinAndCos() {
//  var sin = [], sin2 = [],
//    cos = [];
//
//  for (var i = 1; i < 100; i++) {
//    sin.push({ x: i, y: Math.sin(i / 10) });
//    sin2.push({ x: i, y: i % 10 == 5 ? null : Math.sin(i / 10) * 0.25 + 0.5 });
//    cos.push({ x: i, y: .5 * Math.cos(i / 10 + 2) + Math.random() / 10 });
//  }
//
//  return [
//    {
//      values: sin,      // values - represents the array of {x,y} data points
//      key: 'Sine Wave', // key  - the name of the series.
//      color: '#ff7f0e'  // color - optional: choose your own line color.
//    },
//    {
//      values: cos,
//      key: 'Cosine Wave',
//      color: '#2ca02c'
//    },
//    {
//      values: sin2,
//      key: 'Another sine wave',
//      color: '#7777ff'
////      area: true      //area - set to true if you want this line to turn into a filled area chart.
//    }
//  ];
//}
//function randomNums() {
//  var randomNums = [];
//  for (var i = 1; i < 100; i++) {
//    randomNums.push({x: i, y: getRandomInt(3, 90)});
//  }
//  return [
//    {
//      values: randomNums,      // values - represents the array of {x,y} data points
//      key: 'Response Times', // key  - the name of the series.
//      color: '#ff7f0e'  // color - optional: choose your own line color.
//    }
//  ];
//}
//function getRandomInt(min, max) {
//        return Math.floor(Math.random() * (max - min + 1)) + min;
//}
}
