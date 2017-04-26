import { Parent, Children } from '../models/models';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'discoveryData' })
export class DiscoveryData implements PipeTransform {

  transform(json) {
    let g = json['graph'];
    let r = g['router-edge'];
    let edgeChildren = [];
    for (let edgeChild of r['children']) {
      let nodeChildren = [];
      for (let nodeChild of edgeChild['children']) {
        nodeChildren.push(
          new Children(
            nodeChild.name + '(' + nodeChild.version + ')',
            edgeChild.name,
            10,
            this.getRingColor(nodeChild.status),
            this.getInnerColor(nodeChild.status)
          )
        );
      }
      edgeChildren.push(
        new Parent(
          edgeChild.name + '(' + edgeChild.host + ')',
          'router-edge',
          10,
          'darkgreen',
          'lightgreen',
          nodeChildren
        )
      );
    }
    let routerEdge = new Parent('router-edge', null, 10, 'darkgreen', 'lightgreen', edgeChildren);
    return routerEdge;
  }

  getRingColor(status: string) {
    let color;
    switch (status) {
      case 'UP':
        color = 'darkgreen';
        break;
      case 'DOWN':
        color = '#d62728';
        break;
      case 'STARTING':
        color = '#e6550d';
        break;
      case 'OUT_OF_SERVICE':
        color = 'black';
        break;
      case 'UNKNOWN':
        color = 'gray';
        break;
    }
    return color;
  }

  getInnerColor(status: string) {
    let color;
    switch (status) {
      case 'UP':
        color = 'lightgreen';
        break;
      case 'DOWN':
        color = 'pink';
        break;
      case 'STARTING':
        color = '#fdd0a2';
        break;
      case 'OUT_OF_SERVICE':
        color = 'gray';
        break;
      case 'UNKNOWN':
        color = 'lightgray';
        break;
    }
    return color;
  }
}
