import numpy as np


def distance(p1, p2):
    return np.linalg.norm(p1 - p2)


def read_file(filepath):
    pos = []
    P = []
    E = []
    
    file = open(filepath, 'r')
    a = [float(x) for x in file.readline().split(' ')]
    for i in range(150):
        x, y, p, e = [float(x) for x in file.readline().split(' ')[:-1]]
        pos.append(np.array([x, y]))
        P.append(p)
        E.append(e)
    return [pos, P, E]


def write_file(filepath):
    return