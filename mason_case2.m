clc
clear

% Test case:
% https://electricalacademia.com/control-systems/signal-flow-graphs-and-masons-gain-formula/

% Define transfer functions between each node
syms G1 G2 G3 G4 H1 H2

% Transfer matrix：Nodes = AT x Nodes
% where Nodes = transpose([y1, y2, y3, y4, y5]);
% We label each node in the case from left to right as above.
% R = y1, C = y5.
% Forward gain between nodes are below the diagnal(exclusive).
% Backward gain between nodes and themselfs are above the
% diagnal(inclusive).
AT = [1  0  0   0   0 
     ;1  0  -H2 0   0
     ;0  G1 0   -H1 0
     ;G4 G3 G2  0   0
     ;0  0  0   1   0];

% Size of the square matrix
n = width(AT);

% Eigenvalue calculation equation
% (A - lambda*I)V = 0
% where lambda is the eigen value,
% V is the eigen vector,
% I is the identity matrix,
% A is the targeting matrix.
Aa = eye(n) - AT;

% Calculate transfer function y5/y1:
% Strip the first row and last column of Aa
num = expand(det(Aa(2:end, 1:end-1)))
% Strip the first row and first column of Aa
den = det(Aa(2:end, 2:end))
% Determine the sign
sign = (-1)^(n+1)

% Result, may use for substitution afterwards
sign*num/den
