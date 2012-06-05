(function() {
  var BattleshipBot, Point;
  var __indexOf = Array.prototype.indexOf || function(item) {
    for (var i = 0, l = this.length; i < l; i++) {
      if (this[i] === item) return i;
    }
    return -1;
  };
  Point = (function() {
    function Point(x, y) {
      this.x = x;
      this.y = y;
    }
    Point.prototype.toString = function() {
      return "(" + this.x + "," + this.y + ")";
    };
    return Point;
  })();
  BattleshipBot = (function() {
    function BattleshipBot(size) {
      if (size == null) {
        size = 10;
      }
      this.size = size;
      this.commands = ["miss", "carrier", "battleship", "submarine", "destroyer", "patrol", "hit"];
      this.ships = this.commands.slice(1, 6);
      this.left = this.commands.slice(1, 6);
      this.init();
    }
    BattleshipBot.prototype.suggest = function() {
      var any, current, data, exclude, i, info, item, max, res, result, ship, slot, tile, _i, _j, _k, _l, _len, _len2, _len3, _len4, _len5, _len6, _len7, _len8, _len9, _m, _ref, _ref2, _ref3, _ref4, _ref5, _ref6;
      data = (function() {
        var _results;
        _results = [];
        for (i = 0; i <= 99; i++) {
          _results.push(0);
        }
        return _results;
      })();
      exclude = [];
      result = void 0;
      _ref = this.information;
      for (slot = 0, _len = _ref.length; slot < _len; slot++) {
        info = _ref[slot];
        if (info === "hit") {
          exclude.push(slot);
          _ref2 = this.left;
          for (_i = 0, _len2 = _ref2.length; _i < _len2; _i++) {
            ship = _ref2[_i];
            for (tile = 0, _len3 = data.length; tile < _len3; tile++) {
              current = data[tile];
              if (!(__indexOf.call(exclude, tile) >= 0)) {
                if (this.xproj(slot, tile) && this.yproj(slot, tile)) {
                  _ref3 = this.left;
                  for (_j = 0, _len4 = _ref3.length; _j < _len4; _j++) {
                    any = _ref3[_j];
                    data[tile] = Math.round(data[tile] + this.corr(slot, ship, tile, any));
                  }
                }
              } else {
                data[tile] = -500;
              }
            }
          }
        } else if (info === "miss") {
          exclude.push(slot);
          _ref4 = this.left;
          for (_k = 0, _len5 = _ref4.length; _k < _len5; _k++) {
            ship = _ref4[_k];
            for (tile = 0, _len6 = data.length; tile < _len6; tile++) {
              current = data[tile];
              if (!(__indexOf.call(exclude, tile) >= 0)) {
                data[tile] = Math.round(data[tile] + this.corr(slot, "miss", tile, ship));
              } else {
                data[tile] = -500;
              }
            }
          }
        } else if (info === "water") {
          _ref5 = this.left;
          for (_l = 0, _len7 = _ref5.length; _l < _len7; _l++) {
            ship = _ref5[_l];
            data[slot] = Math.round(data[slot] + this.ship(slot, ship));
          }
        } else if (info !== "water") {
          exclude.push(slot);
          for (tile = 0, _len8 = data.length; tile < _len8; tile++) {
            current = data[tile];
            if (!(__indexOf.call(exclude, tile) >= 0)) {
              _ref6 = this.left;
              for (_m = 0, _len9 = _ref6.length; _m < _len9; _m++) {
                any = _ref6[_m];
                if (info !== any) {
                  data[tile] = Math.round(data[tile] + this.corr(slot, info, tile, any));
                }
              }
            } else {
              data[tile] = -500;
            }
          }
        }
      }
      result = (function() {
        var _len10, _results;
        _results = [];
        for (slot = 0, _len10 = data.length; slot < _len10; slot++) {
          item = data[slot];
          if (item === (max = Math.max.apply(Math, data))) {
            _results.push(slot);
          }
        }
        return _results;
      })();
      console.log("Excludes : " + ((function() {
        var _len10, _n, _results;
        _results = [];
        for (_n = 0, _len10 = exclude.length; _n < _len10; _n++) {
          res = exclude[_n];
          _results.push(this.fromScalar(res));
        }
        return _results;
      }).call(this)));
      this.printData(this.information, "ships");
      this.printData(data, "probabilities");
      console.log("Max probability : " + max + " on slots -> " + ((function() {
        var _len10, _n, _results;
        _results = [];
        for (_n = 0, _len10 = result.length; _n < _len10; _n++) {
          res = result[_n];
          _results.push(this.fromScalar(res));
        }
        return _results;
      }).call(this)));
      return result = this.fromScalar(result[Math.floor(Math.random() * result.length)]);
    };
    BattleshipBot.prototype.xproj = function(slot, tile) {
      return true;
    };
    BattleshipBot.prototype.yproj = function(slot, tile) {
      var i, lower, upper, _ref;
      if (slot % this.size === tile % this.size) {
        lower = Math.min(slot, tile);
        upper = Math.max(slot, tile);
        if (upper - lower > this.size) {
          for (i = 0, _ref = (upper - lower) / this.size; 0 <= _ref ? i <= _ref : i >= _ref; 0 <= _ref ? i++ : i--) {
            if (this.information[lower + this.size * i] === "miss") {
              return false;
            }
          }
        }
      }
      return true;
    };
    BattleshipBot.prototype.printData = function(data, msg) {
      var i, _results;
      console.log("Displaying " + msg + " data :");
      _results = [];
      for (i = 0; i <= 9; i++) {
        _results.push(console.log(data.slice(i * 10, ((i + 1) * 10 - 1 + 1) || 9e9)));
      }
      return _results;
    };
    BattleshipBot.prototype.suggestEmpty = function() {
      var data, i, info, item, max, res, result, ship, slot, _i, _len, _len2, _ref, _ref2;
      data = (function() {
        var _results;
        _results = [];
        for (i = 0; i <= 99; i++) {
          _results.push(0);
        }
        return _results;
      })();
      result = void 0;
      max = 0;
      _ref = this.information;
      for (slot = 0, _len = _ref.length; slot < _len; slot++) {
        info = _ref[slot];
        if (info === "water") {
          _ref2 = this.left;
          for (_i = 0, _len2 = _ref2.length; _i < _len2; _i++) {
            ship = _ref2[_i];
            data[slot] = Math.round(data[slot] + this.ship(slot, ship));
          }
        }
      }
      this.printData(data, "probabilities");
      result = (function() {
        var _len3, _results;
        _results = [];
        for (slot = 0, _len3 = data.length; slot < _len3; slot++) {
          item = data[slot];
          if (item === (max = Math.max.apply(Math, data))) {
            _results.push(slot);
          }
        }
        return _results;
      })();
      console.log("Max probability : " + max + " on slots -> " + ((function() {
        var _j, _len3, _results;
        _results = [];
        for (_j = 0, _len3 = result.length; _j < _len3; _j++) {
          res = result[_j];
          _results.push(this.fromScalar(res));
        }
        return _results;
      }).call(this)));
      return result = this.fromScalar(result[Math.floor(Math.random() * result.length)]);
    };
    BattleshipBot.prototype.update = function(x, y, command) {
      return this.information[this.toScalar(x, y)] = command;
    };
    BattleshipBot.prototype.info = function(slot, command) {
      return this.information[slot] === command;
    };
    BattleshipBot.prototype.corr = function(slot, ship, tile, command) {
      return this.correlation[this.commands.indexOf(ship)][slot][this.commands.indexOf(command) - 1][tile];
    };
    BattleshipBot.prototype.ship = function(slot, ship) {
      return this.background[this.ships.indexOf(ship)][slot];
    };
    BattleshipBot.prototype.toScalar = function(x, y) {
      return x * this.size + y;
    };
    BattleshipBot.prototype.fromScalar = function(i) {
      return new Point(Math.floor(i / this.size), i % this.size);
    };
    BattleshipBot.prototype.init = function() {
      var i;
      this.background = B;
      this.information = (function() {
        var _results;
        _results = [];
        for (i = 0; i <= 99; i++) {
          _results.push("water");
        }
        return _results;
      })();
      return this.correlation = C;
    };
    return BattleshipBot;
  })();
  this.BattleshipBot = BattleshipBot;
}).call(this);
