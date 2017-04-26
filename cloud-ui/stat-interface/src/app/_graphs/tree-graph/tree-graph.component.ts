import { DiscoveryStatsService } from '../../_services/discovery-stats.service';
import { Parent, Children } from '../../models/models';
import { DiscoveryData } from '../../pipes/discovery-data.pipe';
import { Component, OnInit, ElementRef, Inject, ViewEncapsulation, OnDestroy } from '@angular/core';
import { Observable, Subscription } from 'rxjs';

declare var d3: any;

@Component({
  selector: 'app-tree-graph',
  templateUrl: './tree-graph.component.html',
  styleUrls: ['./tree-graph.component.css'],
  providers: [DiscoveryData, DiscoveryStatsService],
  encapsulation: ViewEncapsulation.None
})
export class TreeGraphComponent implements OnInit, OnDestroy {
  e1: ElementRef;
  data;
  margin = { top: 0, right: 60, bottom: 0, left: 135 };
  height = 245;
  width = 580;

  tree;
  diagonal;
  svg;
  root;
  i = 0;
  duration = 0;
  dataPipe: DiscoveryData;
  subscription: Subscription;
  dataSubscription: Subscription;
  constructor( @Inject(ElementRef) elementRef: ElementRef, dataPipe: DiscoveryData, private _discovery: DiscoveryStatsService) {
    this.e1 = elementRef;
    this.dataPipe = dataPipe;
  }

  ngOnInit() {
    this.data = null;
    const timer = Observable.timer(0, 1000);
    this.subscription = timer.subscribe(() => this.getFromDiscovery());
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
    this.dataSubscription.unsubscribe();
  }

  getFromDiscovery() {
    this.dataSubscription = this._discovery.getDiscoveryData().subscribe(
      data => {
        let isDataAlreadyFound: boolean;
        if (this.data) {
          isDataAlreadyFound = true;
        }
        this.data = [];
        this.data.push(this.dataPipe.transform(data));
        this.draw();
      },
      error => console.log(<any>error)
    );
  }

  draw() {
    d3.select('#tree').selectAll('*').remove();
    this.tree = d3.layout.tree().size([this.height, this.width]);
    this.diagonal = d3.svg.diagonal().projection(function(d) { return [d.y, d.x]; });
    this.svg = d3.select('#tree').append('svg')
      .attr('width', this.width + this.margin.right + this.margin.left)
      .attr('height', this.height + this.margin.top + this.margin.bottom)
      .append('g')
      .attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')');

    const source = this.data[0];
    this.root = source;
    source.x0 = this.height / 2;
    source.y0 = 0;

    this.update(source);

    d3.select(this.e1.nativeElement).style('height', '500px');
  }

  update(source: any) {
    let nodes = this.tree.nodes(this.root).reverse();
    let links = this.tree.links(nodes);

    nodes.forEach(function(d) { d.y = d.depth * 225; });
    let node = this.svg.selectAll('g.node').data(nodes);
    let nodeEnter = node.enter().append('g')
      .attr('class', 'node')
      .attr('transform', function(d) { return 'translate(' + source.y0 + ',' + source.x0 + ')'; })
      .on('click', click);
    nodeEnter.append('circle')
      .attr('r', function(d) { return d.value; })
      .style('stroke', function(d) { return d.type; })
      .style('stroke-width', 2.5)
      .style('fill', function(d) { return d.level; })
      .on('click', click);

    nodeEnter.append('text')
      .attr('x', function(d) { return d.children || d._children ? -15 : 15; })
      .attr('dy', '.25em')
      .attr('text-anchor', function(d) { return d.children || d._children ? 'end' : 'start'; })
      .text(function(d) { return d.name; })
      .style('fill-opacity', 10);

    var nodeUpdate = node.transition()
      .duration(this.duration)
      .attr('transform', function(d) { return 'translate(' + d.y + ',' + d.x + ')'; });

    nodeEnter.append('circle')
      .attr('r', function(d) { return d.value; })
      .style('stroke', function(d) { return d.type; })
      .style('stroke-width', 2.5)
      .style('fill', function(d) { return d.level; })
      .on('click', click);

    nodeUpdate.select('text')
      .style('fill-opacity', 10);

    // Transition exiting nodes to the parent's new position.
    var nodeExit = node.exit().transition()
      .duration(this.duration)
      .attr('transform', function(d) { return 'translate(' + d.y + ',' + d.x + ')'; })
      .remove();

    nodeExit.select('circle')
      .attr('r', 10);

    nodeExit.select('text')
      .style('fill-opacity', 10);

    // Update the links…
    var link = this.svg.selectAll('path.link')
      .data(links);

    // Enter any new links at the parent's previous position.
    link.enter().insert('path', 'g')
      .attr('class', 'link')
      .attr('stroke', 'black')
      .attr('stroke-width', '2.5px')
      .attr('d', function(d) {
        var o = { x: source.x0, y: source.y0 };
        return { source: o, target: o };
      });

    // Transition links to their new position.
    link.transition()
      .duration(this.duration)
      .attr('d', this.diagonal);

    // Transition exiting nodes to the parent's new position.
    link.exit().transition()
      .duration(this.duration)
      .attr('d', function(d) {
        var o = { x: source.x, y: source.y };
        return { source: o, target: o };
      })
      .remove();

    // Stash the old positions for transition.
    nodes.forEach(function(d) {
      d.x0 = d.x;
      d.y0 = d.y;
    });
  }

}

function click(d) {
  if (d.children) {
    d._children = d.children;
    d.children = null;
  } else {
    d.children = d._children;
    d._children = null;
  }
  this.update(d);
};
