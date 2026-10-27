-- Execute uma vez no banco antigo. Nenhum registro e apagado.
alter table carro add column if not exists status varchar(30) default 'DISPONIVEL';
update carro set status = 'DISPONIVEL' where status is null;
alter table carro alter column status set not null;

alter table aluguel add column if not exists data_devolucao date;

-- A versao anterior exigia funcionario. O cadastro de aluguel atual nao exige.
-- Mantemos a coluna antiga e os seus dados, apenas permitimos que ela fique vazia.
do $$
begin
    if exists (
        select 1 from information_schema.columns
        where table_schema = 'public' and table_name = 'aluguel'
          and column_name = 'id_funcionario'
    ) then
        alter table aluguel alter column id_funcionario drop not null;
    end if;
end $$;

alter table loja alter column endereco type varchar(200);
alter table carro alter column modelo type varchar(100);
alter table cliente alter column nome type varchar(100);

-- O cadastro atual armazena o CPF apenas com numeros para localizar clientes.
update cliente set cpf = regexp_replace(cpf, '[^0-9]', '', 'g');
