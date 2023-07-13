clc
clear

% Test case:
% https://www.tutorialspoint.com/control_systems/control_systems_masons_gain_formula.htm
% Reference:
% https://ieeexplore.ieee.org/document/9318953
% https://www.researchgate.net/publication/346648788_A_Matrix_Approach_for_Analyzing_Signal_Flow_Graph

% Define transfer functions between each node
syms A B C D E F G H I J

% Transfer matrix：Nodes = AT x Nodes
% where Nodes = transpose([y1, y2, y3, y4, y5, y6]);
% Forward gain between nodes are below the diagnal(exclusive).
% Backward gain between nodes and themselfs are above the
% diagnal(inclusive).
AT = [1 0 0 0 0 0 
     ;A 0 J 0 0 0
     ;0 B 0 0 H 0
     ;0 0 C 0 I 0
     ;0 0 G D F 0
     ;0 0 0 0 E 0];

% Size of the square matrix
n = width(AT);

% Eigenvalue calculation equation
% (A - lambda*I)V = 0
% where lambda is the eigen value,
% V is the eigen vector,
% I is the identity matrix,
% A is the targeting matrix.
Aa = eye(n) - AT;

% Calculate transfer function y6/y1:
% Strip the first row and last column of Aa
num = expand(det(Aa(2:end, 1:end-1)))
% Strip the first row and first column of Aa
den = det(Aa(2:end, 2:end))
% Determine the sign
sign = (-1)^(n+1)

% Result, may use for substitution afterwards
sign*num/den


