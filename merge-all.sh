current_branch=`git rev-parse --abbrev-ref HEAD`
if [ "$current_branch" == "chaudhary" ]; then
    #echo -e 'Checkout to master'
    echo    #Newline
    git checkout master
    echo "Merge chaudhary in ^"
    git merge chaudhary 

    echo    #Newline
    git checkout tanjot 
    echo "Merge master in ^"
    git merge master

    echo -e '\nFinal checkout to chaudhary'
    git checkout chaudhary 

    echo -e '\nPushing all to origin'
    git push origin --all --verbose
else
    echo "Not in branch chaudhary"
fi

