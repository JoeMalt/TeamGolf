 
load signals_thresholded.mat

ObjectiveFunction = @objfun;
startingPoint = [1 0 1];
lb = [0.1 -100 0.5];
ub = [2 100 10];
%plotobjective(ObjectiveFunction,[0.5 -200; 1.5 200]);
%view(-15,150);

options = optimoptions(@simulannealbnd, 'MaxIterations', 1000); 
[x,fval,exitFlag,output] = simulannealbnd(ObjectiveFunction,startingPoint,lb,ub,options)


nOriginalItems = length(original); 
nScanItems = length(scan); 

%transform = @(y)((y-x(2))/x(1)); 
transform = @(y)(x(1)*y + x(2)); 



scan_transformed = NaN(1, nOriginalItems); 


for index = 1:nOriginalItems
    new_index = (floor(transform(index))); 
    if(new_index >= 1) && (new_index <= nScanItems)
        %scan_transformed(index) = original(new_index);
        scan_transformed(index) = scan(new_index) * x(3); 
    end
end

figure
plot (original, 'r')
hold on
plot (scan, 'g')
hold on
plot (scan_transformed, 'b')
hold off 


