export class Parent {
  name;
  parent;
  value;
  type;
  level;
  children: Children[];
  constructor(name, parent, value, type, level, children: Children[]) {
    this.name = name;
    this.parent = parent;
    this.value = value;
    this.type = type;
    this.level = level;
    this.children = children;
  }
}

export class Children {
  name;
  parent;
  value;
  type;
  level;
  constructor(name, parent, value, type, level) {
    this.name = name;
    this.parent = parent;
    this.value = value;
    this.type = type;
    this.level = level;
  }
}
