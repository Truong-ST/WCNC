import numpy as np


class Sensor:
    def __init__(self, position, P, E):
        self.E_max = 10800
        self.E_min = 540
        self.E = E
        self.P = P
        self.position = position
        
        
class WCE:
    def __init__(self):
        self.P_M = 1
        self.E_MC = 50800
        self.U = 10
        self.V = 5