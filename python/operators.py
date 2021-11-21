import numpy as np
from copy import deepcopy
from genetic_algorithm import Individual


def PMX(ind1: Individual, ind2: Individual):
    n = len(ind1.path) # number of gen
    p1 = np.random.randint(n)
    p2 = np.random.randint(n)
    while p1 == p2:
        p2 = np.random.randint(n)
    if p1 > p2:
        p1, p2 = p2, p1
    p2 += 1
    
    offspring2 = deepcopy(ind1)
    offspring1 = deepcopy(ind2)
    mid1 = ind1.path[p1: p2]
    mid2 = ind2.path[p1: p2]

    ## giư lại đoạn giữa
    offspring1.path[p1:p2], offspring2.path[p1:p2] = ind1.path[p1:p2], ind2.path[p1:p2] 
    
    for i in range(p1, p2):
        save_point = ind2.path[i]
        point2 = ind2.path[i]
        index = i
        
        if point2 in mid1:
            continue
        
        while p1 <= index <= p2 -1:
            point1 = ind1.path[index] # điểnm bên trên
            index = ind2.path.index(point1)
            point2 = ind2.path[index]
            
        offspring1.path[index] = save_point
        
        
    for i in range(p1, p2):
        save_point = ind1.path[i]
        point2 = ind1.path[i]
        index = i
        
        if point2 in mid2:
            continue
        
        while p1 <= index <= p2 -1:
            point1 = ind2.path[index]
            index = ind1.path.index(point1)
            point2 = ind1.path[index]
            
        offspring2.path[index] = save_point
    return [offspring1, offspring2]
    
def SPX(ind1: Individual, ind2: Individual):
    n = len(ind1.path)
    p = np.random.randint(n)
    offspring1 = deepcopy(ind1)
    offspring2 = deepcopy(ind2)
    
    tmp_p = 0
    for i in range(p, n):
        for j in range(tmp_p, n):
            if ind2.path[j] in offspring1.path[:i]:
                continue
            offspring1.path[i] = ind2.path[j]
            tmp_p = j
            break
        
    tmp_p = 0
    for i in range(p, n):
        for j in range(tmp_p, n):
            if ind1.path[j] in offspring2.path[:i]:
                continue
            offspring2.path[i] = ind1.path[j]
            tmp_p = j
            break
    
    return offspring1, offspring2

def CIM(ind: Individual):
    offspring = deepcopy(ind)
    p = np.random.randint(len(ind.path))
    head = ind.path[:p]
    tail = ind.path[p:]
    head = head[::-1]
    for i in tail[::-1]:
        head.append(i)
    return offspring

def swap_mutation(ind: Individual):
    N = len(ind.path)
    p1 = np.random.randint(N)
    p2 = np.random.randint(N)
    
    offspring = deepcopy(ind)
    offspring.path[p1], offspring.path[p2] = offspring.path[p2], offspring.path[p1]
    
    return offspring

def SPAH(ind1: Individual, ind2: Individual):
    N = len(ind1.time_charge)
    p1 = np.random.randint(N)
    p2 = np.random.randint(N)
    beta = np.random.uniform(-0.5, 0.5)
    
    offspring1 = deepcopy(ind1)
    offspring2 = deepcopy(ind2)
    
    for i in range(N):
        offspring1.time_charge[i] = beta * ind1.time_charge[i] + (1-beta) * ind2.time_charge[i]
        offspring2.time_charge[i] = beta * ind2.time_charge[i] + (1-beta) * ind1.time_charge[i]
    offspring1.time_charge = np.array(offspring1.time_charge) * np.sum(ind1.time_charge) / np.sum(offspring1.time_charge)
    offspring2.time_charge = np.array(offspring2.time_charge) * np.sum(ind2.time_charge) / np.sum(offspring2.time_charge)
    
    return [offspring1, offspring2]

def mutation2(ind: Individual):
    N = len(ind.time_charge)
    mutate = deepcopy(ind)
    p1 = np.random.randint(N)
    p2 = np.random.randint(N)
    
    delta = np.random.uniform() * 10
    mutate.time_charge[p1] += delta
    mutate.time_charge[p2] -= delta
    
    return ind