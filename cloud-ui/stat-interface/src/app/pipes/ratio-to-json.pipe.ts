import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'ratioToJson'
})
export class RatioToJsonPipe implements PipeTransform {

  transform(value: any, args?: any): Ratio[] {
    let versions: string[] = value[0];
    let ratios = value[1];
    let accuracy = value[2];

    let ratio1: number = ratios[0];
    let ratio2: number = ratios[1];

    let gcdenom: number = gcd(ratio1, ratio2);

    let firstRatio: Ratio = new Ratio(versions[0].replace('v', ''), ratios[0] / gcdenom, accuracy[0]);
    let secondRatio: Ratio = new Ratio(versions[1].replace('v', ''), ratios[1] / gcdenom, accuracy[1]);

    let ratioArray: Ratio[] = [];
    ratioArray.push(firstRatio);
    ratioArray.push(secondRatio);
    return ratioArray;
  }

}

function gcd(a, b) {
  return !b ? a : gcd(b, a % b);
}

export class Ratio {
  private version;
  private trafficRatio;
  private accuracy;
  constructor(version, trafficRatio, accuracy) {
    this.version = version;
    this.trafficRatio = trafficRatio;
    this.accuracy = accuracy;
  }
}
