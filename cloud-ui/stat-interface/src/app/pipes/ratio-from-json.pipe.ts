import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'ratioFromJson'
})
export class RatioFromJsonPipe implements PipeTransform {

  transform(json: any, args?: any): any {
    let firstVersion: string;
    let firstRatio;
    let secondVersion: string;
    let secondRatio;
    for (let ratio of json['ratios']) {
      if (!firstVersion) {
        firstVersion = ratio.version;
        firstRatio = ratio.trafficRatio;
      } else {
        if (parseFloat(firstVersion) >= parseFloat(ratio.version)) {
          secondVersion = firstVersion;
          secondRatio = firstRatio;
          firstVersion = ratio.version;
          firstRatio = ratio.trafficRatio;
        } else {
          secondVersion = ratio.version;
          secondRatio = ratio.trafficRatio;
        }
      }
    }
    let versions = [];
    let ratios = [];
    if (firstRatio && secondRatio) {
      let multiplier = findMultiplier(firstRatio, secondRatio);
      firstRatio = firstRatio * multiplier;
      secondRatio = secondRatio * multiplier;
      if (parseFloat(firstVersion.replace('.', '').replace('.', '')) > parseFloat(secondVersion.replace('.', '').replace('.', ''))) {
        versions.push(secondVersion, firstVersion);
        ratios.push(secondRatio, firstRatio);
      } else {
        versions.push(firstVersion, secondVersion);
        ratios.push(firstRatio, secondRatio);
      }
    } else if (firstRatio) {
      versions.push(firstVersion);
      ratios.push(100);
    } else {
      versions = [];
      ratios = [];
    }
    let versionsToRatios = [versions, ratios];
    return versionsToRatios;
  }

}

function findMultiplier(a, b) {
  return 100 / (a + b);
}
