ObjectiveFunction = @objfun;
startingPoint = [1 0];
lb = [0.5 -200];
ub = [1.5 200];
%plotobjective(ObjectiveFunction,[0.5 -200; 1.5 200]);
%view(-15,150);
[x,fval,exitFlag,output] = simulannealbnd(ObjectiveFunction,startingPoint,lb,ub);


nOriginalItems = length(denoised_original); 
nScanItems = length(denoised_scan); 

A = NaN(1, nOriginalItems); 

%transform = @(y)((y-x(2))/x(1)); 
transform = @(y)(x(1)*y + x(2)); 


for index = 1:nOriginalItems
    if((floor(transform(index)) >= 1) && (floor(transform(index)) <= nScanItems))
        A(index) = denoised_original(floor(transform(index)));
    end
end
