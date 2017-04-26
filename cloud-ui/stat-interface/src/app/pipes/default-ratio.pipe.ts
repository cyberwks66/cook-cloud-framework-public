import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'defaultRatio'
})
export class DefaultRatioPipe implements PipeTransform {

  transform(json, name) {
    let g = json['graph'];
    let r = g['router-edge'];
    let defaultRatios = [50, 50];
    let defaultVersions = [];
    for (let edgeChild of r['children']) {
      for (let nodeChild of edgeChild['children']) {
        if (nodeChild.name === name) {
          if (defaultVersions.length > 0) {
            if (parseFloat(defaultVersions[0]) > parseFloat(nodeChild.version)) {
              defaultVersions.push(nodeChild.version);
              defaultVersions.shift();
            } else if (parseFloat(defaultVersions[0]) > parseFloat(nodeChild.version)) {
              defaultVersions.push(nodeChild.version);
            } else if (parseFloat(defaultVersions[0]) === parseFloat(nodeChild.version)) {
              // do nothing
            }
          } else {
            defaultVersions.push(nodeChild.version);
          }
        }
      }
    }
    let setRatios = [defaultVersions, defaultRatios];
    return setRatios;
  }

}
