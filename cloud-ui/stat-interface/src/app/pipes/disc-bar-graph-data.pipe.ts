import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'discBarGraphData'
})
export class DiscBarGraphDataPipe implements PipeTransform {
  transform(json: any, args?: any): [string[], number[]] {
    let names: string[] = [];
    let counts: number[] = [];
    let g = json['graph'];
    let r = g['router-edge'];
    let edgeCount = 1;
    for (let cluster of r['cluster']) {
      edgeCount++;
    }
    names.push('router-edge');
    counts.push(edgeCount);

    let nodeCount = 0;
    let childNames: string[] = [];
    let childCounts: number[] = [];
    for (let edgeChild of r['children']) {
      nodeCount++;
      for (let nodeChild of edgeChild['children']) {
        let name: string = nodeChild.name;
        if (childNames.indexOf(name) > -1) {
          let index = childNames.indexOf(name);
          childCounts[index] = childCounts[index] + 1;
        } else {
          childNames.push(nodeChild.name);
          childCounts.push(1);
        }
      }
    }
    names.push('router-node');
    counts.push(nodeCount);
    for (let i = 0; i < childNames.length; i++) {
      names.push(childNames[i]);
      counts.push(childCounts[i]);
    }
    let instances: [string[], number[]] = [names, counts];
    return instances;
  }

  countEdge(r) {

  }

}
