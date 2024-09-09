clc
clear

% Transfer Functions
syms A B C D E F G H I J

% Signals
syms y1 y2 y3 y4 y5 y6

signals = transpose([y1, y2, y3, y4, y5, y6]);

% Transfer matrix：Nodes = AT x Nodes
% Forward gain between nodes are below the diagnal(exclusive).
% Backward gain between nodes and themselfs are above the
% diagnal(inclusive).

% Start AT generation
n_AT = length(signals) + 2;

AT = sym('X', [n_AT, n_AT]);
AT(:,:) = 0;
AT(1,1) = 1
% Now we have a fresh AT to start with

% Describe the block diagram
AT = AT_ReplaceElement(AT, signals, y1, y2, A)
AT = AT_ReplaceElement(AT, signals, y2, y3, B)
AT = AT_ReplaceElement(AT, signals, y3, y4, C)
AT = AT_ReplaceElement(AT, signals, y4, y5, D)
AT = AT_ReplaceElement(AT, signals, y5, y6, E)
AT = AT_ReplaceElement(AT, signals, y5, y5, F)
AT = AT_ReplaceElement(AT, signals, y3, y5, G)
AT = AT_ReplaceElement(AT, signals, y5, y3, H)
AT = AT_ReplaceElement(AT, signals, y5, y4, I)
AT = AT_ReplaceElement(AT, signals, y3, y2, J)

% Define input and outputs
AT = AT_DefineInput(AT, signals, y1)
AT = AT_DefineOutput(AT, signals, y6)

AT_ExportCsv(AT, signals, '1.csv')

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
num = det(Aa(2:end, 1:end-1))
% Strip the first row and first column of Aa
den = det(Aa(2:end, 2:end))
% Determine the sign
sign = (-1)^(n+1)

% Result, may use for substitution afterwards
our_result = sign*num/den





function str_n=Num2NameSegment(n)
    if n == 0
        str_n = '_0';
    elseif n > 0
        str_n = ['_p' num2str(abs(round(n)))];
    else
        str_n = ['_n' num2str(abs(round(n)))];
    end
end

function base_name = DF_BaseNameOf(var_w0)
    % Check if the input is a string
    if ischar(var_w0) || isstring(var_w0)
        base_name = var_w0;
    % Check if the input is a symbolic variable
    elseif isa(var_w0, 'sym')
        base_name = char(var_w0);
    % Throw an error for other types of input
    else
        error('Input must be either a string or a symbolic variable.');
    end
end

function name = DF_SignalNameOf(var_w0, sb)
    name = [DF_BaseNameOf(var_w0) Num2NameSegment(sb)];
end

function name = DF_TFNameOf(n_in, n_out, var_w0) 
    name = [DF_BaseNameOf(var_w0) Num2NameSegment(n_in) Num2NameSegment(n_out)];
end


% TF naming convention:
% G_<from>_<to>
% from, to: ..., n2, n1, 0, p1, p2, p3
% E.g. G_p1_p1, G_0_n2
function result = DF_DefineTFMatrix(input_sb, output_sb, var_w0)
    result = sym('X', [length(output_sb), length(input_sb)]);
    for iq=1:length(output_sb)
        for ip=1:length(input_sb)
            p = input_sb(ip);
            q = output_sb(iq);
            result(iq, ip) = DF_TFNameOf(p, q, var_w0);
        end
    end
end

function result=DF_DefineSquareTFMatrix(n_sb, var_w0)
    result = DF_DefineTFMatrix([-n_sb:n_sb], [-n_sb:n_sb], var_w0);
end

% Signal Naming convention:
% V(w)  V(w-ws) V(w+ws) V(w-2ws) V(w+2ws)   ... V(w-n_sb*ws)    V(w+n_sb*ws)
% V     V_n1    V_p1    V_n2    V_p2            V_nn            V_pn
% where V(w) is a function of s, total len=1+2*n_sb
function result=DF_DefineSignals(sb, var_w0)
    result = sym('X', [length(sb) 1]);
    for i=1:length(sb)
        result(i) = DF_SignalNameOf(var_w0, sb(i));
    end
end

function result = DF_DefineSignalVector(n_sb, var_w0)
    result = DF_DefineSignals([-n_sb:n_sb], var_w0);
end

function index = LocateSignalByName(sym_array, name)
    % Convert the symbolic array and variable to their string representations
    sym_array_str = arrayfun(@char, sym_array, 'UniformOutput', false);
    
    % Find the index of the symbolic variable in the array
    index = find(strcmp(sym_array_str, name), 1);

    % Throw an error if the variable is not found
    if isempty(index)
        error('The symbolic variable was not found in the array.');
    end
end

function index = LocateSignal(signals, var_in)
    index = LocateSignalByName(signals, char(var_in));
end

function index = LocateDFSignal(signals, var_w0, sb)
    index = LocateSignalByName(signals, DF_SignalNameOf(var_w0, sb));
end

% The list of signals, excluding the input and output signal.
% The rows and column of the AT matrix is [input <signals> output]
function [i_from, i_to] = AT_LocateElement(signals, var_from, var_to)
    i_from = LocateSignal(signals, var_from) + 1;
    i_to = LocateSignal(signals, var_to) + 1;
end

% Set the element in AT so that it represents the TF:
% var_to/var_from = val
function [new_AT, old_val] = AT_ReplaceElement(AT, signals, var_from, var_to, val)
    [i_from, i_to] = AT_LocateElement(signals, var_from, var_to);
    old_val = AT(i_to, i_from);
    AT(i_to, i_from) = val;
    new_AT = AT;
end

function new_AT = AT_SetDFMatrix(AT, signals, vars_from, vars_to, df_matrix)
    [i_from, i_to] = AT_LocateElement(signals, vars_from(1, 1), vars_to(1, 1));
    AT(i_to:i_to+length(df_matrix)-1, i_from:i_from+length(df_matrix)-1) = df_matrix;
    new_AT = AT;
end

function new_AT = AT_SetMultiFreqTF(AT, signals, vars_from, vars_to, tfs)
    df_matrix = diag(tfs);
    new_AT = AT_SetDFMatrix(AT, signals, vars_from, vars_to, df_matrix);
end

function new_AT = AT_DefineInput(AT, signals, var_in)
    i_in = LocateSignal(signals, var_in) + 1;
    AT(i_in, 1) = 1;
    new_AT = AT;
end

function new_AT = AT_DefineOutput(AT, signals, var_out)
    i_out = LocateSignal(signals, var_out) + 1;
    AT(length(AT), i_out) = 1;
    new_AT = AT;
end

function AT_ExportCsv(AT, signals, filename)
    % Check if the input is a symbolic matrix
    if ~isa(AT, 'sym')
        error('Input must be a symbolic matrix.');
    end

    syms INPUT OUTPUT;

    AT = [[INPUT; signals; OUTPUT] AT];
    AT = [transpose([0; INPUT; signals; OUTPUT]); AT];

    % Convert symbolic matrix to a cell array of strings
    sym_cell = arrayfun(@char, AT, 'UniformOutput', false);

    title = sym_cell(1, 1);
    title{1} = 'To\From';
    sym_cell(1, 1) = title;

    % Export the cell array to a CSV file using writecell
    writecell(sym_cell, filename);
    
    fprintf('Symbolic matrix successfully exported to %s\n', filename);
end

